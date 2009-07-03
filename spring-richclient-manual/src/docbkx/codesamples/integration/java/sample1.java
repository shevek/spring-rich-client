public class DemoPerspectiveFactory implements PerspectiveFactory, InitializingBean {
    private List<String> dockableIds;

    public Perspective getPerspective(String perspectiveId) {

        Perspective perspective = new Perspective(perspectiveId, perspectiveId);
        LayoutSequence sequence = perspective.getInitialSequence(true);

        String prevDockableId = null;
        for (String dockableId : this.dockableIds) {
            sequence.add(dockableId, prevDockableId);
            prevDockableId = dockableId;
        }

        return perspective;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(this.dockableIds, "No dockable ids specified");
    }

    public List<String> getDockableIds() {
        return dockableIds;
    }

    public void setDockableIds(List<String> dockableIds) {
        this.dockableIds = dockableIds;
    }
}