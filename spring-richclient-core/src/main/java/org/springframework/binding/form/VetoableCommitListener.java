package org.springframework.binding.form;

public interface VetoableCommitListener
{
    public boolean proceedWithCommit(FormModel formModel);
}

