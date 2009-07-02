MessageBoxCommand command = new MessageBoxCommand();
commandConfigurer = (CommandConfigurer) ApplicationServicesLocator.services().getService(
                    CommandConfigurer.class);
commandConfigurer.configure(command);