#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import json
import os
import cgi
import datetime
import webapp2

from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext.webapp import template

class Aim( db.Model ):
    author = db.UserProperty()
    user = db.StringProperty()
    latitude = db.FloatProperty()
    longitude = db.FloatProperty()
    message = db.StringProperty( multiline = True )

    def to_string( self ):
        data = { "author": self.author, "user": self.user, "latitude": self.latitude, "longitude": self.longitude, "message": self.message }
        return json.dumps( data )


class MainPage(webapp2.RequestHandler):
    def get(self):
        path = os.path.join(os.path.dirname(__file__), 'index.html')
        template_values = {}
        self.response.out.write(template.render(path, template_values))


class SetPoint(webapp2.RequestHandler):
    def post(self):
        aim = Aim()
        aim.author = users.get_current_user()
        aim.user = self.request.get( "user" )
        aim.latitude = float( self.request.get( "lat" ) )
        aim.longitude = float( self.request.get( "lon" ) )
        aim.message = self.request.get( "message" )
        aim.put()

        self.response.out.write( "Point is set" )

class GetPoint(webapp2.RequestHandler):
    def get(self):
        aim = Aim.all().filter( "user =", self.request.get( "user" ) )
        if aim:
            self.response.out.write( aim[ 0 ].to_string() )
        else:
            self.response.out.write( "EMPTY" )


app = webapp2.WSGIApplication([
    ( '/', MainPage ),
    ( '/setpoint', SetPoint ),
    ( '/getpoint', GetPoint ),
], debug=True)

