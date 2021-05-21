import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import javax.swing.JFormattedTextField.AbstractFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;


/**
 * This class represents the RecordsPanel in the window
 * which will displays the attendance
 * @author xinyu zhao
 *
 */
public class RecordsPanel extends JPanel {
   private SQLiteManager sqlManager;
   private String dataDir;
 
   private List<Classroom> allClassrooms;
   private DefaultListModel<String> classroomComboModel;
   
   private DefaultListModel<String> listPresentModel = new DefaultListModel<String>();
   private JList<String> listPresent;

   private DefaultListModel<String> listAbsentModel = new DefaultListModel<String>();
   private JList<String> listAbsent;

   private GridBagConstraints constraints = new GridBagConstraints();
 
   private JButton showButton = new JButton("Show Attendence");
   
   static String datePattern = "yyyy-MM-dd";
   static SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

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
 
       JComboBox<String> classroomCombo;
   	   DefaultComboBoxModel<String> classroomComboModel = new DefaultComboBoxModel<String>();

       if (allClassrooms != null) {
           for(Classroom classroom : allClassrooms) {
        	   classroomComboModel.addElement(classroom.getCourseName());
           }
       }
       classroomCombo = new JComboBox<String>(classroomComboModel);
       
       UtilDateModel model = new UtilDateModel();
       model.setValue(new Date());
       Properties p = new Properties();
       p.put("text.today", "Today");
       p.put("text.month", "Month");
       p.put("text.year", "Year");
       JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
       JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

       cmdPanel.add(classroomCombo);
       cmdPanel.add(datePicker);
       
       showButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
        	   int classroomIndex = classroomCombo.getSelectedIndex();
        	   Classroom classroom = allClassrooms.get(classroomIndex);
        	   Date dt = model.getValue();
        	   List<Integer> presentStudentIDs = sqlManager.getStudentAttendence(classroom.getId(), dt);
        	   List<Student> presentStudents = new ArrayList<Student>();
        	   if (presentStudentIDs != null) {
        		   for(int id : presentStudentIDs) {
        			   Student student = sqlManager.getStudent(id);
        			   if (student != null) {
        				   presentStudents.add(student);
        			   }
        		   }
        	   }
        	   listAbsentModel.removeAllElements();
        	   listPresentModel.removeAllElements();
        	   List<Integer> ids = sqlManager.getStudentsInClassroom(classroom.getId());
        	   if (ids != null) {
        		   for(int id : ids) {
        			   boolean isPresent = false;
        			   for(Student student : presentStudents) {
        				   if (student.getID() == id) {
        					   isPresent = true;
        					   break;
        				   }
        			   }
        			   if (!isPresent) {
        				   Student absentStudent = sqlManager.getStudent(id);
        				   if (absentStudent != null) {
        					   listAbsentModel.addElement(absentStudent.getName());
        				   }
        			   }
        		   }
        	   }
			   for(Student student : presentStudents) {
				  listPresentModel.addElement(student.getName());
			   }
           }
       });
 
	   listPresent = new JList<String>(listPresentModel);
	   listPresent.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	   listPresent.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	   listPresent.setVisibleRowCount(-1);
	   JScrollPane listPresentScroller = new JScrollPane(listPresent);
	   listPresentScroller.setBorder(BorderFactory.createTitledBorder ("Present students"));
	   listPresentScroller.setPreferredSize(new Dimension(400, 300));

	   listAbsent = new JList<String>(listAbsentModel);
	   listAbsent.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	   listAbsent.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	   listAbsent.setVisibleRowCount(-1);
	   JScrollPane listAbsentScroller = new JScrollPane(listAbsent);
	   listAbsentScroller.setBorder(BorderFactory.createTitledBorder ("Absent students"));
	   listAbsentScroller.setPreferredSize(new Dimension(400, 300));

 
       GridBagConstraints gbc = new GridBagConstraints();
       gbc.fill = GridBagConstraints.NONE;
       gbc.weightx = 0;
       gbc.gridx = 0;
       gbc.gridy = 0;
 
       add(cmdPanel, gbc);
 
       gbc.fill = GridBagConstraints.VERTICAL;
       gbc.weighty = 1;
       gbc.weightx = 1;
       gbc.gridx = 1;
       gbc.gridy = 0;
       add(listPresentScroller, gbc);

       gbc.gridx = 2;
       add(listAbsentScroller, gbc);
   }
}

class DateLabelFormatter extends AbstractFormatter {

	@Override
    public Object stringToValue(String text) throws ParseException {
        return RecordsPanel.dateFormatter.parseObject(text);
    }
 
    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return RecordsPanel.dateFormatter.format(cal.getTime());
        }
         
        return "";
    }
 
}

