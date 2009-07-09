public interface FormComponentInterceptor {
    public void processLabel(String propertyName, JComponent label);

    public void processComponent(String propertyName, JComponent component);
}
