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

        # Search appropriately for gathers
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

        # Get all the gathers
        # who have already started and are not ignored

        # Sort gathers by location

        # Make arrays to pass back
        names = []
        latitudes = []
        longitudes = []
        end_times = []
        user_statuses = []

        dict_passed = {
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
    R = 6373

    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = (math.sin(dlat/2))**2 + math.cos(lat1) * math.cos(lat2) * (math.sin(dlon/2))**2
    c = 2 * math.atan2( math.sqrt(a), math.sqrt(1-a) )
    d = R * c #(where R is the radius of the Earth)
    return d


# Show the gathers the pertain to an individual
class MyGathers (webapp2.RequestHandler):
    def get(self):
        # Identify the user
        user = identify_user(self.request.get('number'))

        # Get all the gathers the person is apart of, no need for ignored here
        # User status will now just be determined by which list
        # Owned
        # Going
        # Invited
        # Interested

        dict_passed = {
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
    ('/', MainPage),
    ], debug=True)