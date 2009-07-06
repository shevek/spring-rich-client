Rules rules = new Rules(Person.class);
rules.add(new RequiredIfTrue("driverLicenseNumber", new PropertyValueConstraint("age",
                new Constraint()
                {
                    public boolean test(final Object argument)
                    {
                        return ((Integer) argument).intValue() >= 21;
                    }
                })));
