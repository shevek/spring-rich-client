package org.springframework.richclient.widget.editor.provider;

public interface DataProviderEventSource
{
    public abstract void addDataProviderListener(DataProviderListener dataProviderListener);

    public abstract void removeDataProviderListener(DataProviderListener dataProviderListener);

}
