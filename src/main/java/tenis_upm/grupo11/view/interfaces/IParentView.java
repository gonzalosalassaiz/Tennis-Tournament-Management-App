package tenis_upm.grupo11.view.interfaces;

/**
 * Represents a parent view, which has "children" panels among
 * which it can switch.
 */
public interface IParentView {

    /**
     * Maps the child panels to its corresponding codename
     */
    void mapChildPanels();

    /**
     * Switches the actual parent with the one identified by the codename
     *
     * @param panelCodename the codename identifying the panel
     */
    void switchPanel(String panelCodename);
}