package org.springframework.richclient.widget;

import org.springframework.richclient.command.ActionCommand;

/**
 * Widget dat een selectie van objecten kan doorgeven. Bv dialoog met table waarin
 * een selectie wordt gemaakt.
 *
 * @author jh
 */
public interface SelectionWidget extends Widget
{
    /**
     * Widget kan de selectie maken en dan via een trigger een bijbehorend commando uitvoeren.
     * Bv. tabel met dubbelclick-listener kan commando activeren en zo dialog sluiten
     *
     * @param command
     */
    void setSelectionCommand(ActionCommand command);

    /**
     * Verwijderen van selectionCommand.
     */
    void removeSelectionCommand();

    /**
     * @return De selectie die gemaakt is.
     */
    Object getSelection();
}
