/*
 * Copyright 2021 James F. Bowring and CIRDLES.org.
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
package org.cirdles.squid;

import java.io.IOException;
import java.nio.file.Path;
import org.cirdles.squid.projects.Squid3ProjectBasicAPI;

/**
 *
 * @author bowring
 */
public interface Squid3API {

    public Squid3ProjectBasicAPI getSquid3Project();
    
    public void openSquid3Project(Path projectFilePath);

    public void openDemonstrationSquid3Project()throws IOException;
    
    public void generateAllSquid3ProjectReports()throws IOException;
}
