/*
 * Copyright (C) 2012-2013 Gregory S. Meiste  <http://gregmeiste.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meiste.greg.ptwgame;

import com.google.gson.annotations.Expose;

public final class Driver {
    @Expose
    public int mNumber;

    @Expose
    public String mFirstName;

    @Expose
    public String mLastName;

    public String getName() {
        return mFirstName + " " + mLastName;
    }
}
