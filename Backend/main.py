import webapp2
import os
import urllib
import jinja2
import datetime
import cgi
import re
import json
import random
import time
import math

from google.appengine.api import users
from google.appengine.ext import ndb
from google.appengine.ext import blobstore
from google.appengine.api import mail
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api.images import get_serving_url
from google.appengine.api import files, images


# Constants for parameters passed
NUMBER = 'number'
LATITUDE = 'latitude'
LONGITUDE = 'longitude'
GATHER_ID = 'gatherid'
NAME = 'name'
START_TIME = 'start_time'
END_TIME = 'end_time'
DESCRIPTION = 'description'
USER_STATUS = 'user_status'
VISIBILITY = 'visibility'
INVITE_LEVEL = 'invite_level'
USERS_INVITED = 'users_invited'

TERMS = 'terms'

PUBLIC = 'public'
PRIVATE = 'private'

IGNORE = 'ignore'
GOING = 'going'
INVITED = 'invited'
INTERESTED = 'interested'

PARSE_PATTERN = "%Y-%m-%d %H:%M:%S.%f"


# For each user, identified by their phone_number
class User(ndb.Model):
    name = ndb.StringProperty()
    phone_number = ndb.StringProperty()  # This is the key
    squads = ndb.KeyProperty(repeated=True)
    gathers_owned = ndb.KeyProperty(repeated=True)
    gathers_going = ndb.KeyProperty(repeated=True)
    gathers_invited = ndb.KeyProperty(repeated=True)
    gathers_ignored = ndb.KeyProperty(repeated=True)
    gathers_interested = ndb.KeyProperty(repeated=True)


# For each gather, identified by name
class Gather(ndb.Model):
    name = ndb.StringProperty()  # This is the key
    latitude = ndb.FloatProperty()
    longitude = ndb.FloatProperty()
    start_time = ndb.DateTimeProperty()
    end_time = ndb.DateTimeProperty()
    description = ndb.StringProperty()
    admins = ndb.KeyProperty(repeated=True)
    users_going = ndb.KeyProperty(repeated=True)
    users_invited = ndb.KeyProperty(repeated=True)
    users_ignored = ndb.KeyProperty(repeated=True)
    users_interested = ndb.KeyProperty(repeated=True)
    visibility = ndb.StringProperty()  # "public" or "private"
    invite_level = ndb.StringProperty()
    picture = ndb.BlobKeyProperty()
    distance = ndb.FloatProperty()


# For each squad, identified by name
class Squad(ndb.Model):
    admins = ndb.KeyProperty(repeated=True)
    members = ndb.KeyProperty(repeated=True)
    name = ndb.StringProperty()  # This is the key
    picture = ndb.BlobKeyProperty()
    description = ndb.StringProperty()


def identify_user(number):
    """

    :rtype : a user object
    """
    user_key = ndb.Key(User, number)
    return user_key.get()


def identify_gather(gather_id):
    gather_key = ndb.Key(Gather, gather_id)
    return gather_key.get()


# Removes the duplicates in a list
def remove_dups(sequence):
    unique = []
    [unique.append(item) for item in sequence if item not in unique]
    return unique


# Given a gather and a user, returns invited, ignored, interested, or going
def find_user_status(gather, user):
    if gather.key in user.gathers_ignored:
        return IGNORE
    elif gather.key in user.gathers_invited:
        return INVITED
    elif gather.key in user.gathers_interested:
        return INTERESTED
    elif gather.key in user.gathers_going:
        return GOING


# Return a datetime from a string
def string_to_datetime(string_date):
    return datetime.datetime.strptime(string_date, PARSE_PATTERN)


# Search by search terms
class Search (webapp2.RequestHandler):
    def get(self):
        # Get the search terms
        terms = self.request.get(TERMS)

        # Identify the user
        user = identify_user(self.request.get(NUMBER))

        # Search appropriately for gathers by name in this order
        query_list = terms.replace(',', '').split(" ")
        gather_query = Gather.query()
        for query in query_list:
            if query != '':
                gather_query = gather_query.filter(Gather.name == query)
        # 1) Gathers that are public OR they are invited to
        gather_query = gather_query.filter(ndb.OR(Gather.visibility == PUBLIC, Gather.key.IN(user.gathers_invited)))
        # 2) Gathers that aren't ignored
        # gather_query = gather_query.filter(Gather.key not in user.gathers_ignored)  # I wish
        gathers = gather_query.fetch()
        for gather in gathers:
            if gather.key in user.gathers_ignored:
                gathers.remove(gather)

        # Sort gathers by start time (sooner first)
        gathers = sorted(gathers, key=lambda k: k.start_time,reverse = False)

        # Create arrays to pass back
        names = []
        latitudes = []
        longitudes = []
        start_times = []
        end_times = []
        user_statuses = []

        for gather in gathers:
            names.append(gather.name)
            latitudes.append(gather.latitude)
            longitudes.append(gather.longitude)
            start_times.append(str(gather.start_time))
            end_times.append(str(gather.end_time))
            user_statuses.append(find_user_status(gather, user))

        dict_passed = {
            NAME+'s': names,
            LATITUDE+'s': latitudes,
            LONGITUDE+'s': longitudes,
            START_TIME+'s': start_times,
            END_TIME+'s': end_times,
            USER_STATUS+'es': user_statuses,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# For when a gather is created, put the info in the database
# SMS invites/notifications are done with Android
class CreateGather (webapp2.RequestHandler):
    def post(self):
        # Get the name
        gather_name = self.request.params[NAME]  # If it's post, .get(NAME) for get

        # Make sure the name for the gather hasn't already been used
        gather_query = Gather.query(Gather.name == gather_name)
        gathers = gather_query.fetch(400)
        if gathers:
            result = False  # Return that the gather couldn't be made
        else:
            # Aggregate the basic data
            gather = Gather(id=gather_name)
            gather.name = gather_name
            gather.latitude = float(self.request.params[LATITUDE])
            gather.longitude = float(self.request.params[LONGITUDE])
            gather.description = self.request.params[DESCRIPTION]
            gather.visibility = self.request.params[VISIBILITY]

            # Format start & end times
            gather.start_time = string_to_datetime(self.request.params[START_TIME])
            gather.end_time = string_to_datetime(self.request.params[END_TIME])

            # Make current user an admin
            user = identify_user(self.request.params[NUMBER])
            admin = [user]
            gather.admins.append(user.key)

            # Still missing picture, invite_level

            # Add the gather to the list of gathers that the current user owns
            owned = user.gathers_owned
            owned.append(gather.key)
            user.gathers_owned = owned

            # Automatically set the owner to going
            add_to_list(user, gather, "going")

            # Add the users that are invited to users_invited
            users_invited_string = self.request.params[USERS_INVITED]
            users_invited = users_invited_string.split('+')

            for invited in users_invited:
                user_invited = identify_user(invited)
                # If they aren't a user, make them one
                if user_invited is None:
                    user_invited = User(id=invited, phone_number=invited)
                    user_invited.put()
                # Add the gather to the list of gathers they are invited to
                user_gather_invited = user_invited.gathers_invited
                user_gather_invited.append(gather.key)
                user_invited.gathers_invited = user_gather_invited
                user_invited.put()

                # Add the user to the list of users that are invited
                gather_invited = gather.users_invited
                gather_invited.append(user_invited.key)
                gather.users_invited = gather_invited

            # Put everything in the database!
            user.put()
            gather.put()

            result = True  # Return that it was made successfully, yay

        # Return true or false (if the gather was successfully made)
        dict_passed = {
            'result': result,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# Pick the user list based on the status keyword
def pick_user_list(user, status):
    if status == GOING:
        return user.gathers_going
    elif status == INVITED:
        return user.gathers_invited
    elif status == INTERESTED:
        return user.gathers_interested
    elif status == IGNORE:
        return user.gathers_ignored
    else:
        return None


# Put the user list back into the user
def set_user_list(user, user_list, status):
    if status == GOING:
        user.gathers_going = user_list
    elif status == INVITED:
        user.gathers_invited = user_list
    elif status == INTERESTED:
        user.gathers_interested = user_list
    elif status == IGNORE:
        user.gathers_ignored = user_list
    else:
        return None


# Pick the user list based on the status keyword
def pick_gather_list(gather, status):
    if status == GOING:
        return gather.users_going
    elif status == INVITED:
        return gather.users_invited
    elif status == INTERESTED:
        return gather.users_interested
    elif status == IGNORE:
        return gather.users_ignored
    else:
        return None


# Put the user list back into the user
def set_gather_list(gather, gather_list, status):
    if status == GOING:
        gather.users_going = gather_list
    elif status == INVITED:
        gather.users_invited = gather_list
    elif status == INTERESTED:
        gather.users_interested = gather_list
    elif status == IGNORE:
        gather.users_ignored = gather_list
    else:
        return None


# Adds a user to gather list based on status and
#  adds gather to user list based on status
def add_to_list(user, gather, status):

    gather_list = pick_gather_list(gather, status)
    gather_list.append(user.key)
    gather_list = remove_dups(gather_list)
    set_gather_list(gather, gather_list, status)

    user_list = pick_user_list(user, status)
    user_list.append(gather.key)
    user_list = remove_dups(user_list)
    set_user_list(user, user_list, status)


# Removes a user from gather list based on status and
#  removes gather from user list based on status
def remove_from_list(user, gather, status):

    gather_list = pick_gather_list(gather, status)
    if user.key in gather_list:
        gather_list.remove(user.key)
    gather_list = remove_dups(gather_list)
    set_gather_list(gather, gather_list, status)

    user_list = pick_user_list(user, status)
    if gather.key in user_list:
        user_list.remove(gather.key)
    user_list = remove_dups(user_list)
    set_user_list(user, user_list, status)


# Gets the other three statuses when given one
def other_statuses(status):
    list_stats = [GOING, INVITED, INTERESTED, IGNORE]
    list_stats.remove(status)
    return list_stats


# Changes a user's status with regards to a gather
class ChangeStatus (webapp2.RequestHandler):
    def get(self):
        # Identify the user
        user = identify_user(self.request.get(NUMBER))

        # Identify the gather
        gather = identify_gather(self.request.get(GATHER_ID))

        # Get the new status
        new_status = self.request.get('status')

        # Get the user and change the list the gather is on
        # Get the gather and change the list the user is on
        # Add it to new list
        add_to_list(user, gather, new_status)

        # Remove it from other list
        other_stats = other_statuses(new_status)
        for stat in other_stats:
            remove_from_list(user, gather, stat)

        # Put the user and the gather back into the database
        user.put()
        gather.put()

        dict_passed = {
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# Give all the information about a gather back
class ViewGather (webapp2.RequestHandler):
    def get(self):

        # Identify the user
        user = identify_user(self.request.get(NUMBER))

        # Identify which gather
        gather = identify_gather(self.request.get(GATHER_ID))

        # Create variables to pass back
        name = gather.name
        latitude = gather.latitude
        longitude = gather.longitude
        start_time = str(gather.start_time)
        end_time = str(gather.end_time)
        description = gather.description
        visibility = gather.visibility
        invite_level = gather.invite_level

        # Extract the more complicated variables
        # If the current user is an admin
        admin = False
        if user.key in gather.admins:
            admin = True

        # The picture url

        # The current user's status
        user_status = find_user_status(gather, user)

        dict_passed = {
            NAME: name,
            LATITUDE: latitude,
            LONGITUDE: longitude,
            START_TIME: start_time,
            END_TIME: end_time,
            USER_STATUS: user_status,
            DESCRIPTION: description,
            VISIBILITY : visibility,
            INVITE_LEVEL : invite_level,
            'admin': admin,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# Give the gathers that are nearby
class WhatsHappening (webapp2.RequestHandler):
    def get(self):
        # Identify the user
        user = identify_user(self.request.get(NUMBER))

        # Get current location
        current_latitude = self.request.get(LATITUDE)
        current_longitude = self.request.get(LONGITUDE)

        # Get all the gathers that have, filtered in this order
        gather_query = Gather.query()
        #  1) already started but not ended, time NOW > time 4 seconds ago: start < now < end
        now = datetime.datetime.now()
        gather_query = gather_query.filter(ndb.AND(Gather.start_time < now, Gather.end_time > now))
        #  2) public OR the user is invited
        gather_query = gather_query.filter(ndb.OR(Gather.visibility == PUBLIC, Gather.key.IN(user.gathers_invited)))
        #  3) are not ignored
        # gather_query = gather_query.filter(Gather.key not in user.gathers_ignored)  # I wish
        gather_list = gather_query.fetch()
        for gather in gather_list:
            if gather.key in user.gathers_ignored:
                gather_list.remove(gather)

        gathers = []
        # Give each gather a distance value
        for gather in gather_list:
            gather.distance = calc_dist(current_latitude, current_longitude,
                                        gather.latitude, gather.longitude)
            gather.put()
            gathers.append(gather)

        # Sort gathers by distance from current location
        gathers = sorted(gathers, key=lambda k: k.distance,reverse = False)

        # Make arrays to pass back
        names = []
        latitudes = []
        longitudes = []
        end_times = []
        user_statuses = []

        for gather in gathers:
            names.append(gather.name)
            latitudes.append(gather.latitude)
            longitudes.append(gather.longitude)
            end_times.append(str(gather.end_time))
            user_statuses.append(find_user_status(gather, user))

        dict_passed = {
            NAME+'s': names,
            LATITUDE+'s': latitudes,
            LONGITUDE+'s': longitudes,
            END_TIME+'s': end_times,
            USER_STATUS+'es': user_statuses,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# Calculates the distance between two sets of latitudes & longitudes
# Currently returns in km
def calc_dist(latitude1, longitude1, latitude2, longitude2):
    lat1 = math.radians(float(latitude1))
    lon1 = math.radians(float(longitude1))
    lat2 = math.radians(float(latitude2))
    lon2 = math.radians(float(longitude2))
    r = 6373

    d_lon = lon2 - lon1
    d_lat = lat2 - lat1
    a = (math.sin(d_lat/2))**2 + math.cos(lat1) * math.cos(lat2) * (math.sin(d_lon/2))**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    d = r * c  # (where R is the radius of the Earth)
    return d


# Show the gathers as they pertain to an individual
class MyGathers (webapp2.RequestHandler):
    def get(self):
        # Identify the user
        user = identify_user(self.request.get(NUMBER))

        # Get all the gathers the person is apart of, no need for ignored here
        # Aggregate them all into one list
        gathers = []
        # Owned
        user_owned = user.gathers_owned
        for owned in user_owned:
            gathers.append(owned.get())
        # Going
        user_going = user.gathers_going
        for going in user_going:
            gathers.append(going.get())
        # Invited
        user_invited = user.gathers_invited
        for invited in user_invited:
            gathers.append(invited.get())
        # Interested
        user_interested = user.gathers_interested
        for interested in user_interested:
            gathers.append(interested.get())

        # Remove any duplicates
        gathers = remove_dups(gathers)

        # Remove gathers that the end time is already past
        # Sooner times are < later times
        # So if end_time < now, remove it
        right_now = str(datetime.datetime.now())
        gathers = [x for x in gathers if not str(x.end_time) < right_now]

        # Sort gathers by start time
        gathers = sorted(gathers, key=lambda k: k.start_time, reverse=False)

        # Create arrays to pass back
        names = []
        latitudes = []
        longitudes = []
        start_times = []
        end_times = []
        user_statuses = []

        for gather in gathers:
            names.append(gather.name)
            latitudes.append(gather.latitude)
            longitudes.append(gather.longitude)
            start_times.append(str(gather.start_time))
            end_times.append(str(gather.end_time))
            user_statuses.append(find_user_status(gather, user))

        dict_passed = {
            NAME+'s': names,
            LATITUDE+'s': latitudes,
            LONGITUDE+'s': longitudes,
            START_TIME+'s': start_times,
            END_TIME+'s': end_times,
            USER_STATUS+'es': user_statuses,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# See if a person is a user already	
class Login (webapp2.RequestHandler):
    def get(self):

        # See if the current user is in the datastore
        number = self.request.get(NUMBER)
        user = identify_user(number)

        # If they are not in the database, add them by number and return None
        if user is None:
            new_user = User(id=number, phone_number=number)
            new_user.put()
            result = None

        # They were invited to something but haven't used the app yet
        elif user.name is None:
            result = None

        # If they are in the database, return their name
        else:
            result = user.name

        dict_passed = {
            'name': result,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# Since the person isn't a user, get their name in the database
class SignUp (webapp2.RequestHandler):
    def get(self):

        # Identify the current user, we put them in there with Login
        number = self.request.get(NUMBER)
        user = identify_user(number)

        # If, for some reason, the user isn't already in the data base, add them
        if user is None:
            new_user = User(id=number, phone_number=number)
            new_user.put()
            user = new_user

        # Set the user's name
        user.name = self.request.get(NAME)

        # Put the user back into the data store
        user.put()

        # Return true on success
        result = True

        dict_passed = {
            'result': result,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


class Purge(webapp2.RequestHandler):
    def get(self):
        try:
            query = blobstore.BlobInfo.all()
            blobs = query.fetch(400)
            index = 0
            if len(blobs) > 0:
                for blob in blobs:
                    blob.delete()
                    index += 1

            hour = datetime.datetime.now().time().hour
            minute = datetime.datetime.now().time().minute
            second = datetime.datetime.now().time().second
            blob_message = (str(index) + ' items deleted from Blobstore at ' + str(hour) + ':' + str(minute) + ':' + str(second)+'\n\n')
            if index == 400:
                self.redirect("/purge")

        except Exception, e:
            # self.response.out.write('Error is: ' + repr(e) + '\n')
            pass

        try:
            user_query = User.query()
            users = user_query.fetch(400)
            index = 0
            if len(users) > 0:
                for result in users:
                    result.key.delete()
                    index += 1

            hour = datetime.datetime.now().time().hour
            minute = datetime.datetime.now().time().minute
            second = datetime.datetime.now().time().second
            user_message = (str(index) + ' items deleted from User at ' + str(hour) + ':' + str(minute) + ':' + str(second)+'\n\n')
            if index == 400:
                self.redirect("/purge")

        except Exception, e:
            # self.response.out.write('Error is: ' + repr(e) + '\n')
            pass

        try:
            gather_query = Gather.query()
            gathers = gather_query.fetch(400)
            index = 0
            if len(gathers) > 0:
                for result in gathers:
                    result.key.delete()
                    index += 1

            hour = datetime.datetime.now().time().hour
            minute = datetime.datetime.now().time().minute
            second = datetime.datetime.now().time().second
            gather_message = (str(index) + ' items deleted from Gather at ' + str(hour) + ':' + str(minute) + ':' + str(second)+'\n\n')
            if index == 400:
                self.redirect("/purge")

        except Exception, e:
            # self.response.out.write('Error is: ' + repr(e) + '\n')
            pass

        try:
            squad_query = Squad.query()
            squads = squad_query.fetch(400)
            index = 0
            if len(squads) > 0:
                for result in squads:
                    result.key.delete()
                    index += 1

            hour = datetime.datetime.now().time().hour
            minute = datetime.datetime.now().time().minute
            second = datetime.datetime.now().time().second
            squad_message = (str(index) + ' items deleted from Squad at ' + str(hour) + ':' + str(minute) + ':' + str(second)+'\n\n')
            if index == 400:
                self.redirect("/purge")

        except Exception, e:
            # self.response.out.write('Error is: ' + repr(e) + '\n')
            pass

        dict_passed ={
            'blob_message': blob_message,
            'user_message': user_message,
            'gather_message': gather_message,
            'squad_message': squad_message,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.write('Hello World!')


class Template (webapp2.RequestHandler):
    def get(self):
        list_ex = []
        dict_passed = {
            'list': list_ex,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# Testing purposes!
class Thing(ndb.Model):
    time = ndb.DateTimeProperty()


class DateTimeTest (webapp2.RequestHandler):
    def get(self):
        string_date = '2015-11-18 01:34:00.0'
        result = str(datetime.datetime.now())
        result2 = str(string_to_datetime(string_date))
        thing = Thing()
        thing.time = datetime.datetime.now()
        thing.put()
        result3 = str(thing.time)
        thing.time = string_to_datetime(string_date)
        thing.put()
        result4 = str(thing.time)
        time_s = 'Time'
        dict_passed = {
            time_s + ' NOW': result,
            time_s + ' from String': result2,
            time_s + ' stored': result3,
            time_s + ' stored string': result4,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


class QueryTest(webapp2.RequestHandler):
    def get(self):
        # Have to put id in Gather constructor!
        test1 = Gather(name="Test 1", id="Test 1")
        test1.put()

        test2 = Gather()
        test2.name = "Test 2"
        test2.id = "Test 2"
        test2.put()

        gather_query = Gather.query()
        query_type = str(type(gather_query))
        gathers = gather_query.fetch()
        gathers_type = str(type(gathers))

        names = []
        for gather in gathers:
            names.append(gather.name)

        dict_passed = {
            'query_type': query_type,
            'gathers_type': gathers_type,
            'names': names,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)

app = webapp2.WSGIApplication([
    ('/search', Search),
    ('/creategather', CreateGather),
    ('/changestatus', ChangeStatus),
    ('/viewgather', ViewGather),
    ('/whatshappening', WhatsHappening),
    ('/mygathers', MyGathers),
    ('/login', Login),
    ('/signup', SignUp),
    ('/purge', Purge),
    ('/', MainPage),
    ('/testdatetime', DateTimeTest),
    ('/testquery', QueryTest),
    ], debug=True)