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
    time_start = ndb.DateTimeProperty()
    time_end = ndb.DateTimeProperty()
    description = ndb.StringProperty()
    admins = ndb.KeyProperty(repeated=True)
    users_going = ndb.KeyProperty(repeated=True)
    users_invited = ndb.KeyProperty(repeated=True)
    users_ignored = ndb.KeyProperty(repeated=True)
    users_interested = ndb.KeyProperty(repeated=True)
    visibility = ndb.StringProperty()
    invite_level = ndb.StringProperty()
    picture = ndb.BlobKeyProperty()


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


# Search by search terms
class Search (webapp2.RequestHandler):
    def get(self):
        # Get the search terms
        terms = self.request.get('terms')

        # Identify the user
        user = identify_user(self.request.get('number'))

        # Search appropriately for gathers by name in this order
        # 1) Gathers that are public
        # 2) Gathers the person is invited to
        # 3) Gathers that aren't ignored

        # Sort gathers by start time (sooner first)

        # Create arrays to pass back
        names = []
        latitudes = []
        longitudes = []
        start_times = []
        end_times = []
        user_statuses = []

        dict_passed = {
            'names': names,
            'latitudes': latitudes,
            'longitudes': longitudes,
            'start_times': start_times,
            'end_times': end_times,
            'user_statuses': user_statuses,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# For when a gather is created, put the info in the database
class CreateGather (webapp2.RequestHandler):

    # Get all the gather information

    # Make sure the name for the gather hasn't already been used

    # Create the gather

    # Add the gather to the list of gathers that the current user owns

    # Return true or false (if the gather was successfully made)

    def get(self):
        dict_passed = {
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# Give all the information about a gather back
class ViewGather (webapp2.RequestHandler):
    def get(self):

        # Identify the user
        user = identify_user(self.request.get('number'))

        # Identify which gather
        gather = identify_gather(self.request.get('gatherid'))

        # Create variables to pass back
        name = gather.name
        latitude = gather.latitude
        longitude = gather.longitude
        time_start = gather.time_start
        time_end = gather.time_end
        description = gather.description
        visibility = gather.visibility
        invite_level = gather.invite_level

        # Extract the more complicated variables
        # If the current user is an admin

        # The picture url

        # The current user's status

        dict_passed = {
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# Give the gathers that are nearby
class WhatsHappening (webapp2.RequestHandler):
    def get(self):
        # Identify the user
        user = identify_user(self.request.get('number'))

        # Get current location
        current_latitude = self.request.get('latitude')
        current_longitude = self.request.get('longitude')

        # Get all the gathers that have, filtered in this order
        #  1) already started
        #  2) public OR the user is invited
        #  3) are not ignored

        # Sort gathers by distance from current location

        # Make arrays to pass back
        names = []
        latitudes = []
        longitudes = []
        end_times = []
        user_statuses = []

        dict_passed = {
            'names': names,
            'latitudes': latitudes,
            'longitudes': longitudes,
            'end_times': end_times,
            'user_statuses': user_statuses,
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
        user = identify_user(self.request.get('number'))

        # Get all the gathers the person is apart of, no need for ignored here
        # Aggregate them all into one list

        # Owned
        # Going
        # Invited
        # Interested

        # Create arrays to pass back
        names = []
        latitudes = []
        longitudes = []
        start_times = []
        end_times = []
        user_statuses = []

        dict_passed = {
            'names': names,
            'latitudes': latitudes,
            'longitudes': longitudes,
            'start_times': start_times,
            'end_times': end_times,
            'user_statuses': user_statuses,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# See if a person is a user already	
class Login (webapp2.RequestHandler):
    def get(self):

        # See if the current user is in the datastore
        number = self.request.get('number')
        user = identify_user(number)

        # If they are not in the database, add them by number and return None
        if user is None:
            new_user = User(id=number, phone_number=number)
            new_user.put()
            result = None

        # If they are in the database, return their name
        else:
            result = user.name

        dict_passed = {
            'result': result,
        }
        json_obj = json.dumps(dict_passed, sort_keys=True, indent=4, separators=(',', ': '))
        self.response.write(json_obj)


# Since the person isn't a user, get their name in the database
class SignUp (webapp2.RequestHandler):
    def get(self):

        # Identify the current user, we put them in there with Login
        user = identify_user(self.request.get('number'))

        # Set the user's name
        user.name = self.request.get('name')

        # Put the user back into the datastore
        user.put()

        # Return true on success
        result = True

        dict_passed = {
            'result': result,
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


app = webapp2.WSGIApplication([
    ('/search', Search),
    ('/creategather', CreateGather),
    ('/viewgather', ViewGather),
    ('/whatshappening', WhatsHappening),
    ('/mygathers', MyGathers),
    ('/login', Login),
    ('/signup', SignUp),
    ('/', MainPage),
    ], debug=True)