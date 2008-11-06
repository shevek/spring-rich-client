package org.springframework.richclient.components;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;

/** Factory that creates a hashmap for use with a JSlider and its labelMap. */
public class SliderLabelFactory
{

    private Map<Integer, String> labels;

    /** Creates a new SliderLabelFactory */
    public SliderLabelFactory()
    {
        labels = new HashMap<Integer, String>();
    }

    /** Sets the string labels for specific values. */
    public void setLabels(Map<Integer, String> labels)
    {
        this.labels = labels;
    }

    /** Gets a map with integer values with the corresponding JLabel for that value */
    public Hashtable<Integer, JLabel> getSliderLabels()
    {
        Hashtable<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        for (Map.Entry<Integer, String> entry : labels.entrySet())
        {
            dict.put(entry.getKey(), new JLabel(entry.getValue()));
        }
        return dict;
    }

}