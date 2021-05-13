import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
 
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * This class represents the RecordsPanel in the window
 * which will displays the attendance
 * @author xinyu zhao
 *
 */
public class RecordsPanel extends JPanel {
   private SQLiteManager sqlManager;
   private String dataDir;
 
   List<Classroom> allClassrooms;
   DefaultListModel<String> classroomListModel;
   JList<String> classroomList;
 
   GridBagConstraints constraints = new GridBagConstraints();
 
   static JButton showButton = new JButton("Show Attendence");
 
   /**
  * this is a RecordsPanel, that creates a Panel called Records on the GUI interface.
  * @param dataDir data directory (sqlite, face images, meta data)
  * @param sqlManager database manager
  */
   public RecordsPanel(String dataDir, SQLiteManager sqlManager) {
       this.dataDir = dataDir;
       this.sqlManager = sqlManager;
      
       setLayout(new GridBagLayout());
      
       allClassrooms = sqlManager.getAllClassrooms();
 
       JPanel cmdPanel = new JPanel();
       cmdPanel.setLayout(new GridLayout(3, 1));
       cmdPanel.add(showButton);
 
       classroomListModel = new DefaultListModel<String>();
       if (allClassrooms != null) {
           for(Classroom classroom : allClassrooms) {
               classroomListModel.addElement(classroom.getCourseName());
           }
       }
       classroomList = new JList<String>(classroomListModel);
      
       showButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
           }
       });
 
 
       classroomList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
       classroomList.setLayoutOrientation(JList.VERTICAL);
       classroomList.setVisibleRowCount(-1);
       JScrollPane listScroller = new JScrollPane(classroomList);
       listScroller.setPreferredSize(new Dimension(200, 200));
 
       GridBagConstraints gbc = new GridBagConstraints();
       gbc.fill = GridBagConstraints.NONE;
       gbc.weightx = 0;
       gbc.gridx = 0;
       gbc.gridy = 0;
 
       add(cmdPanel, gbc);
 
       gbc.fill = GridBagConstraints.VERTICAL;
       gbc.weighty = 1.0;
       gbc.weightx = 1;
       gbc.gridx = 1;
       gbc.gridy = 0;
       add(listScroller, gbc);
   }
}
