MyObject object = new MyObject();
object.setXyz("xyz");
ValidatingFormModel model = new DefaultFormModel(object);
model.getValueModel("xyz").setValue("abc");
// object hasn't changed, object.getXyz() will return xyz
model.commit();
// object has changed, object.getXyz() will return abc