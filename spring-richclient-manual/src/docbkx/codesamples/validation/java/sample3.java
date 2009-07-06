public class RcpConstraint extends AbstractPropertyConstraint
{
    protected boolean test(final PropertyAccessStrategy domainObjectAccessStrategy)
    {
        Object prop = domainObjectAccessStrategy.getPropertyValue(getPropertyName());
        return !(prop instanceof String) || ((String) prop).equals("RCP");
    }
}