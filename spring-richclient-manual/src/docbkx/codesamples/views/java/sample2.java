private NewCommandExecutor newContactExecutor = new NewContactExecutor();
...
protected void registerLocalCommandExecutors(PageComponentContext context)
{
    context.register("newContactCommand", newContactExecutor);
}