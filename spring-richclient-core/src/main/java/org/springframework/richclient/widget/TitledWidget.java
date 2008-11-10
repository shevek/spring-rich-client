package org.springframework.richclient.widget;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.richclient.core.DescriptionConfigurable;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.richclient.form.ValidationResultsReporter;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.richclient.image.config.ImageConfigurable;

/**
 * Provides the basic {@link Widget} facilities in combination with a fully configurable title/message
 * component.
 */
public interface TitledWidget
        extends
        Widget,
        Guarded,
        Messagable,
        TitleConfigurable,
        ImageConfigurable,
        DescriptionConfigurable,
        BeanNameAware
{
    ValidationResultsReporter newSingleLineResultsReporter(Messagable messagable);

    String getId();

}
