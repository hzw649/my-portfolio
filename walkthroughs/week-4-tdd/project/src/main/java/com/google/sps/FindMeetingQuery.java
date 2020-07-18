// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<TimeRange> result = new ArrayList<>();
        int start = -1;
        int end = -1;
        while (start<1440)
        {
            int new_start = end+1;
            int new_end = TimeRange.END_OF_DAY+1;
            for (Event event : events)
                if (!Collections.disjoint(event.getAttendees(), request.getAttendees()))
                {
                    TimeRange timeRange = event.getWhen();
                    if (timeRange.contains(new_start))
                        new_start = timeRange.end();
                    if (timeRange.start() > end && timeRange.start() < new_end)
                        new_end = timeRange.start();
                }
            if (new_end - new_start >= request.getDuration())
                result.add(TimeRange.fromStartEnd(new_start, new_end, false));
            start = new_start;
            end = new_end;
        }
        return result;
    }
}
