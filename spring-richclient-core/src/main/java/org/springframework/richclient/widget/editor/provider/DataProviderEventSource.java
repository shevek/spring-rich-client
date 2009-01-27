package org.springframework.richclient.widget.editor.provider;

public interface DataProviderEventSource
{

    /**
     * Registreer een listener op deze provider om veranderingen
     */
    public abstract void addDataProviderListener(DataProviderListener dataProviderListener);

    /**
     * Verwijder deze listener uit de lijst van geregistreerde observers
     */
    public abstract void removeDataProviderListener(DataProviderListener dataProviderListener);

}
