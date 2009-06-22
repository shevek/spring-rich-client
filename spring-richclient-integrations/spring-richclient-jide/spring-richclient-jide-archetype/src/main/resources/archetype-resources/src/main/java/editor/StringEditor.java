package editor;

import com.jidesoft.spring.richclient.docking.editor.AbstractEditor;

import javax.swing.JComponent;
import javax.swing.JTextArea;

/**
 * <p/>
 * Date: 06.10.2006<br>
 * Time: 13:50:30<br>
 *
 * @author <a href="http://johannes-schneider.info">Johannes Schneider</a> -
 *         <a href="http://www.xore.de">Xore Systems</a>
 */
public class StringEditor extends AbstractEditor {
  private String id;
  private String content;
  private JTextArea textArea;

  @Override
  public String getDisplayName() {
    return "A Text editor";
  }

  @Override
  public void initialize( Object object ) {
    if ( !( object instanceof String ) ) {
      throw new IllegalArgumentException( "Editor object should be a String" );
    }
    content = ( String ) object;
  }

  @Override
  public JComponent getControl() {
    if ( textArea == null ) {
      textArea = new JTextArea();
      textArea.setText( content );
    }
    return textArea;
  }

  @Override
  public String getId() {
    if ( id == null ) {
      id = getDescriptor().getId() + Math.random();
    }
    return id;
  }
}
