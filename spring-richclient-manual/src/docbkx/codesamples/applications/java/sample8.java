public class MultipleToolbarApplicationWindowFactory implements ApplicationWindowFactory
{
    public ApplicationWindow createApplicationWindow()
    {
        return new MultipleToolbarApplicationWindow();
    }
}