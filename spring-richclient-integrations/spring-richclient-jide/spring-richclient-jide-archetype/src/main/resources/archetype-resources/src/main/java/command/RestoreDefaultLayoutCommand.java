/*
 * Copyright 2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package command;

import com.jidesoft.docking.DockingManager;
import com.jidesoft.spring.richclient.docking.JideApplicationPage;
import com.jidesoft.spring.richclient.docking.JideApplicationWindow;
import com.jidesoft.spring.richclient.docking.LayoutManager;
import com.jidesoft.spring.richclient.perspective.Perspective;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;

/**
 * Simple command to reload the JIDE layout from a store. Simple
 * delegates to the LayoutManager.
 *
 * @author Jonny Wray
 */
public class RestoreDefaultLayoutCommand extends ApplicationWindowAwareCommand {
  private static final Log log = LogFactory.getLog( RestoreDefaultLayoutCommand.class );
  private static final String ID = "restoreDefaultLayoutCommand";

  public RestoreDefaultLayoutCommand() {
    super( ID );
  }

  @Override
  protected void doExecuteCommand() {
    log.debug( "Execute command" );
    DockingManager manager = ( ( JideApplicationWindow ) getApplicationWindow() ).getDockingManager();
    Perspective perspective = ( ( JideApplicationPage ) getApplicationWindow().getPage() ).getPerspectiveManager().getCurrentPerspective();
    LayoutManager.loadPageLayoutData( manager, getApplicationWindow().getPage().getId(), perspective );
  }
}
