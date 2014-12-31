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

public final class Race {
    public static final int NAME_SHORT = 0;
    public static final int NAME_LONG = 1;

    private int mId;
    private int mRaceNum;
    private String mTrackLong;
    private String mTrackShort;
    private String mName;
    @SuppressWarnings("unused") private String mTv;
    @SuppressWarnings("unused") private String mSize;
    private long mStart;
    private long mQuestion;
    @SuppressWarnings("unused") private String mLayout;

    public static Race getInstance(final int id) {
        return Races.get()[id];
    }

    public boolean isFuture() {
        return System.currentTimeMillis() < mStart;
    }

    public boolean inProgress() {
        return (mQuestion < System.currentTimeMillis()) && isFuture();
    }

    public boolean isExhibition() {
        return mRaceNum <= 0;
    }

    public boolean isInChase() {
        return mRaceNum >= 27;
    }

    public int getId() {
        return mId;
    }

    public String getTrack(final int length) {
        return (length == NAME_SHORT) ? mTrackShort : mTrackLong;
    }

    public String getName() {
        return mName;
    }
}
