DefaultRulesSource source = new DefaultRulesSource();
Rules rules = new Rules(TestObject.class);
Constraints c = new Constraints();
rules.add(c.required("field1"));
source.addRules(rules);