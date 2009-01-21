/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.application.mdi;

import org.springframework.richclient.application.*;
import org.springframework.richclient.application.mdi.contextmenu.DesktopCommandGroupFactory;
import org.springframework.richclient.application.support.AbstractApplicationPage;
import org.springframework.richclient.util.Assert;
import org.springframework.richclient.util.PopupMenuMouseListener;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Peter De Bruycker
 */
public class DesktopApplicationPage extends AbstractApplicationPage implements PageLayoutBuilder {

	private JDesktopPane control;

	private Map frames = new HashMap();

	private int dragMode;

    private boolean scrollable = true;

    private final DesktopCommandGroupFactory desktopCommandGroupFactory;

	public DesktopApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor, int dragMode,
			DesktopCommandGroupFactory desktopCommandGroupFactory) {
		super(window, pageDescriptor);
		this.desktopCommandGroupFactory = desktopCommandGroupFactory;

		Assert.isTrue(dragMode == JDesktopPane.LIVE_DRAG_MODE || dragMode == JDesktopPane.OUTLINE_DRAG_MODE,
				"dragMode must be JDesktopPane.LIVE_DRAG_MODE or JDesktopPane.OUTLINE_DRAG_MODE");

		this.dragMode = dragMode;
	}

    public void setScrollable(boolean scrollable) {
        if (isControlCreated()) {
            throw new IllegalStateException("scrollable-property can only be set before creation of control");
        }
        this.scrollable = scrollable;
    }

	protected boolean giveFocusTo(PageComponent pageComponent) {
		if (getActiveComponent() == pageComponent) {
			return true;
		}

		JInternalFrame frame = getInternalFrame(pageComponent);
		if (frame == null) {
			return false;
		}

		try {
			if (frame.isIcon()) {
				frame.setIcon(false);
			}

			frame.setSelected(true);
		}
		catch (PropertyVetoException e) {
			// ignore
		}

		return pageComponent.getControl().requestFocusInWindow();
	}

	public void addView(String viewDescriptorId) {
		showView(viewDescriptorId);
	}

	protected void doAddPageComponent(PageComponent pageComponent) {
		JInternalFrame frame = createInternalFrame(pageComponent);

		frame.setVisible(true);
		control.add(frame);
	}

	protected JInternalFrame createInternalFrame(final PageComponent pageComponent) {
		JInternalFrame internalFrame = new JInternalFrame(pageComponent.getDisplayName());
		internalFrame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

		configureFrame(pageComponent, internalFrame);

		keepFrameDetails(pageComponent, internalFrame);

		internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				close(pageComponent);
			}

			public void internalFrameActivated(InternalFrameEvent e) {
				if (!e.getInternalFrame().isIcon()) {
					setActiveComponent(pageComponent);
				}
			}
		});

		internalFrame.getContentPane().add(pageComponent.getControl());
		internalFrame.pack();
		return internalFrame;
	}

    /**
     * Having this method allows subclasses to enrich/wrap the internal frame, for instance with a visible resizer.
     */
    protected void keepFrameDetails(final PageComponent pageComponent, JInternalFrame internalFrame) {
        frames.put(pageComponent, internalFrame);
    }

    protected void configureFrame(PageComponent component, JInternalFrame frame) {
		if (component.getIcon() != null) {
			frame.setFrameIcon(component.getIcon());
		}

		ViewDescriptor descriptor = getViewDescriptor(component.getId());
		if (descriptor instanceof DesktopViewDescriptor) {
			DesktopViewDescriptor desktopViewDescriptor = (DesktopViewDescriptor) descriptor;
			frame.setResizable(desktopViewDescriptor.isResizable());
			frame.setMaximizable(desktopViewDescriptor.isMaximizable());
			frame.setIconifiable(desktopViewDescriptor.isIconifiable());
			frame.setClosable(desktopViewDescriptor.isClosable());
		}
		else {
			frame.setResizable(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setClosable(true);
		}
	}

	protected JInternalFrame getInternalFrame(PageComponent pageComponent) {
		return (JInternalFrame) frames.get(pageComponent);
	}

	protected void doRemovePageComponent(PageComponent pageComponent) {
		// not used
		JInternalFrame frame = getInternalFrame(pageComponent);
		if (frame != null) {
			frame.dispose();
			frames.remove(pageComponent);
		}
	}

	protected JComponent createControl() {
		control = createDesktopPane();
		control.setDragMode(dragMode);

		getPageDescriptor().buildInitialLayout(this);

        if (scrollable) {
            return new JScrollPane(control);
        } else {
            return control;
        }
	}
	
    protected JDesktopPane createDesktopPane() {
        final JDesktopPane control;
        if (scrollable) {
            control = new ScrollingDesktopPane();
        } else {
            control = new JDesktopPane();
        }
        control.addMouseListener(new PopupMenuMouseListener() {
            protected JPopupMenu getPopupMenu() {
                return desktopCommandGroupFactory.createContextMenuCommandGroup(getWindow().getCommandManager(),
                        control).createPopupMenu();
            }
        });
        return control; 
    }

	protected void updatePageComponentProperties(PageComponent pageComponent) {
		JInternalFrame frame = getInternalFrame(pageComponent);

		if (pageComponent.getIcon() != null) {
			frame.setFrameIcon(pageComponent.getIcon());
		}
		frame.setTitle(pageComponent.getDisplayName());
		frame.setToolTipText(pageComponent.getCaption());
	}

	/**
	 * Overridden so it will leave iconified frames iconified.
	 */
	protected void setActiveComponent() {
		// getAllFrames returns the frames in z-order (i.e. the first one in the
		// list is the last one used)
		JInternalFrame[] frames = control.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			JInternalFrame frame = frames[i];
			if (!frame.isIcon()) {
				try {
					frame.setSelected(true);
				}
				catch (PropertyVetoException ignore) {

				}
				break;
			}
		}
	}
}
