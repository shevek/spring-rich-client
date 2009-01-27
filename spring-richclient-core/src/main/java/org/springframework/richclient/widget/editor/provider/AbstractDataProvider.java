/**
 * Created on 24-feb-2005
 * Created by jh
 */
package org.springframework.richclient.widget.editor.provider;

import java.util.Observable;

/**
 * Base implementation for DataProviders.
 * 
 * @author Jan Hoskens
 * 
 */
public abstract class AbstractDataProvider extends Observable implements DataProvider
{

    private final String id;

    /**
     * Constructor zonder specifieke id.
     */
    public AbstractDataProvider()
    {
        this("abstractDataProvider");
    }

    /**
     * Constructor met specifieke id.
     * 
     * @param id
     */
    public AbstractDataProvider(String id)
    {
        this.id = id;
    }

    /**
     * @return id van deze dataProvider.
     */
    public String getId()
    {
        return id;
    }

    /**
     * A basic implementation that directs the necessary logic to {@link #isDetailObject(Object)} and
     * {@link #loadDetailObject(Object)}.
     */
    public final Object getDetailObject(Object selectedObject, boolean forceLoad)
    {
        if (forceLoad || !isDetailObject(selectedObject))
            return loadDetailObject(selectedObject);
        return selectedObject;
    }

    /**
     * Check if the given object is a detailed object or not. If it is already detailed, the object can be
     * returned as-is instead of loading it from the back-end. 
     * 
     * @param objectToCheck
     *            object to check.
     * @return <code>true</code> if the object is a detailed one.
     */
    protected boolean isDetailObject(Object objectToCheck)
    {
        return false;
    }

    /**
     * Load the detailed object from the back-end. Note that although the baseObject can be detailed, you MUST
     * fetch the object from the back-end in any case in this method.
     * 
     * @param baseObject
     *            object containing enough information to fetch a detailed object from the back-end.
     * @return the detailed object retrieved from the back-end.
     */
    protected Object loadDetailObject(Object baseObject)
    {
        if (!hasSpecificDetail())
        {
            return baseObject;
        }
        return getDetailObject(baseObject);
    }

    /**
     * For backwards compatibility, this method is deprecated and is used in the default implementation of
     * {@link #loadDetailObject(Object)}. Please refactor your code and use {@link #loadDetailObject(Object)}
     * and {@link #isDetailObject(Object)}. These methods provide the necessary logic for the new
     * {@link #getDetailObject(Object, boolean)} method.
     */
    @Deprecated
    public boolean hasSpecificDetail()
    {
        return false;
    }

    /**
     * For backwards compatibility, this method is deprecated and is used in the default implementation of
     * {@link #loadDetailObject(Object)}. Please refactor your code and use {@link #loadDetailObject(Object)}
     * and {@link #isDetailObject(Object)}. These methods provide the necessary logic for the new
     * {@link #getDetailObject(Object, boolean)} method.
     * 
     * @throws UnsupportedOperationException
     */
    @Deprecated
    public Object getDetailObject(Object baseObject)
    {
        throw new UnsupportedOperationException("getDetailObject(object) not implemented for " + baseObject);
    }

    /**
     * Basisimplementat die een foutmelding opgooit wanneer deze niet is overschreven.
     * 
     * @throws UnsupportedOperationException
     */
    public Object clone(Object sampleData)
    {
        throw new UnsupportedOperationException("clone(object) not implemented for " + sampleData);
    }

    /**
     * Algemene afhandeling van save functionaliteit. Zet de changed markering, roept de specifieke
     * implementatie van {@link #doUpdate(Object)} op en zend een event naar alle observers.
     * 
     * @param updatedData
     *            het object met de nieuwe data dat moet worden bewaard.
     */
    public final Object update(Object updatedData)
    {
        setChanged();
        Object newEntity = doUpdate(updatedData);
        notifyObservers(DataProviderEvent.updateEntityEvent(updatedData, newEntity));
        return newEntity;
    }

    /**
     * Specifieke afhandeling van de save. Changes en notify worden elders afgehandelt.
     * 
     * @param updatedData
     *            het object met de nieuwe data dat moet worden bewaard.
     * @return Het vernieuwde object.
     */
    public Object doUpdate(Object updatedData)
    {
        throw new UnsupportedOperationException("doUpdate(object) not implemented for " + updatedData);
    };

    /**
     * Algemene afhandeling van delete functionaliteit. Zet de changed markering, roept de specifieke
     * implementatie van {@link #doDelete(Object)} op en zend een event naar alle observers.
     * 
     * @param dataToRemove
     *            het object dat moet worden verwijderd.
     */
    public final void delete(Object dataToRemove)
    {
        setChanged();
        doDelete(dataToRemove);
        notifyObservers(DataProviderEvent.deleteEntityEvent(dataToRemove));
    }

    /**
     * Specifieke afhandeling van de delete. Changes en notify worden elders afgehandelt.
     * 
     * @param dataToRemove
     *            het object dat moet worden verwijderd.
     */
    public void doDelete(Object dataToRemove)
    {
        throw new UnsupportedOperationException("doDelete(object) not implemented for " + dataToRemove);
    }

    /**
     * Algemene afhandeling van create functionaliteit. Zet de changed markering, roept de specifieke
     * implementatie van {@link #doCreate(Object)} op en zend een event naar alle observers.
     * 
     * @param newData
     *            nieuw object dat moet worden aangemaakt.
     */
    public final Object create(Object newData)
    {
        setChanged();
        Object newEntity = doCreate(newData);
        notifyObservers(DataProviderEvent.newEntityEvent(newEntity));
        return newEntity;
    }

    /**
     * Specifieke afhandeling van de create. Changes en notify worden elders afgehandelt.
     * 
     * @param newData
     *            nieuw object dat moet worden aangemaakt.
     * @return Het nieuwe object.
     */
    public Object doCreate(Object newData)
    {
        throw new UnsupportedOperationException("doCreate(object) not implemented for " + newData);
    }

    public Object newInstance(Object criteria)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void addDataProviderListener(DataProviderListener dataProviderListener)
    {
        addObserver(dataProviderListener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeDataProviderListener(DataProviderListener dataProviderListener)
    {
        deleteObserver(dataProviderListener);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsBaseCriteria()
    {
        return false;
    }

    /**
     * Basisimplementat die een foutmelding opgooit wanneer deze niet is overschreven.
     * 
     * @throws UnsupportedOperationException
     */
    public void setBaseCriteria(Object criteria)
    {
        throw new UnsupportedOperationException("setBaseCriteria(object) not implemented for " + criteria);
    }

    public boolean exists(Object data)
    {
        return false;
    }

    public RefreshPolicy getRefreshPolicy()
    {
        return RefreshPolicy.ON_USER_SWITCH;
    }

    public Object getSimpleObject(Object baseObject)
    {
        return baseObject;
    }
}