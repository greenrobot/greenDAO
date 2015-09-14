/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package de.greenrobot.performance.parse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        deleteDatabase("ParseOfflineStore");

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
//    Parse.initialize(this);
        Parse.initialize(this, "X9MEmCvnlX9oGRLmVhunkatw33jlF7wMPZZFw8lZ", "FKI8s0UnK6nT6PdGVO2XgKlcsPZnGJlI8qoPpUKa");


        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
