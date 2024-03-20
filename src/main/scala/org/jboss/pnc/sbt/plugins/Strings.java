/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2021 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.pnc.sbt.plugins;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class Strings {
    /**
     * Converts string with key:value1,key2:value2 to a map of key values
     * items.
     */
    public static Map<String, String> toMap(String string) {
        if (string == null || string.equals("")) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        try {
            String[] pairs = string.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                map.put(keyValue[0], keyValue[1]);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid key:value string: [" + string + "]", e);
        }
        return map;
    }

}
