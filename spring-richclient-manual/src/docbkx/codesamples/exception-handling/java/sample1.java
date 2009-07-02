protected boolean onFinish() {
    try {
        form.getFormModel().commit();
    // ...
    getApplicationContext().publishEvent(new LifecycleApplicationEvent(eventType, getEditingContact()));
    return true;
    } catch (Throwable throwable) {
        handleException(throwable);
    }
}