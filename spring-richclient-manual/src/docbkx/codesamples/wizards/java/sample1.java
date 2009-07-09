public class MyWizard extends AbstractWizard
{
    private MyForm form1;
    private MyOtherForm form2;

    public MyWizard()
    {
        initializeForms();
        addForm(form1);
        addForm(form2);
    }

    protected boolean onFinish()
    {
        form1.commit();
        form2.commit();
        doSomeLogic();
    }
}