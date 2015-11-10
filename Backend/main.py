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


class User(ndb.Model):
    name = ndb.StringProperty()
    has_app = ndb.BooleanProperty()
    phone_number = ndb.StringProperty()
    squads = ndb.KeyProperty(repeated=True)
    gathers_owned = ndb.KeyProperty(repeated=True)
    gathers_going = ndb.KeyProperty(repeated=True)
    gathers_invited = ndb.KeyProperty(repeated=True)
    gathers_ignored = ndb.KeyProperty(repeated=True)
    gathers_interested = ndb.KeyProperty(repeated=True)


class Gather(ndb.Model):
    name = ndb.StringProperty()
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


class Squad(ndb.Model):
    admins = ndb.KeyProperty(repeated=True)
    members = ndb.KeyProperty(repeated=True)
    name = ndb.StringProperty()
    picture = ndb.BlobKeyProperty()
    description = ndb.StringProperty()


class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.write('Hello World!')


class mViewNearby (webapp2.RequestHandler):
    def get(self):
        dictPassed = {
        }
        jsonObj = json.dumps(dictPassed, sort_keys=True,indent=4, separators=(',', ': '))
        self.response.write(jsonObj)

app = webapp2.WSGIApplication([
    ('/', MainPage),
    ], debug=True)