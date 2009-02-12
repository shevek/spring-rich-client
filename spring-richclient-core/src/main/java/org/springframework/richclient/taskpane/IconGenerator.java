package org.springframework.richclient.taskpane;

import javax.swing.*;

public interface IconGenerator<T>
{
    public ImageIcon generateIcon(T forObject);
}
