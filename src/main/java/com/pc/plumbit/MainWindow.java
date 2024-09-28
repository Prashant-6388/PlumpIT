/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pc.plumbit;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Prashant
 */
public class MainWindow extends javax.swing.JFrame {
    
    private static final Logger log = LoggerFactory.getLogger(MainWindow.class);        
        
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width =(int) screenSize.getWidth()/2;
        int height =(int) screenSize.getHeight()/2;
        int x_point = width - this.getWidth()/2;
        int y_point = height - this.getHeight()/2;
        setLocation(x_point,y_point);
        this.setResizable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuWindowPanel = new javax.swing.JPanel();
        contactPane = new javax.swing.JPanel();
        labelWebsite = new javax.swing.JLabel();
        labelPhone = new javax.swing.JLabel();
        labelCompanyName = new javax.swing.JLabel();
        labelEmail = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuItemNew = new javax.swing.JMenuItem();
        menuItemOpen = new javax.swing.JMenuItem();
        menuItemClose = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        contactUsMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImages(null);
        setMinimumSize(new java.awt.Dimension(200, 200));
        setSize(new java.awt.Dimension(200, 200));

        menuWindowPanel.setOpaque(false);

        contactPane.setBackground(new java.awt.Color(204, 255, 204));
        contactPane.setAlignmentX(0.0F);
        contactPane.setAlignmentY(0.0F);
        contactPane.setOpaque(false);
        contactPane.setLayout(null);

        labelWebsite.setFont(new java.awt.Font("Perpetua", 1, 14)); // NOI18N
        labelWebsite.setForeground(new java.awt.Color(61, 0, 246));
        labelWebsite.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelWebsite.setText("www.company-site.com");
        contactPane.add(labelWebsite);
        labelWebsite.setBounds(310, 210, 220, 40);

        labelPhone.setFont(new java.awt.Font("Perpetua", 1, 14)); // NOI18N
        labelPhone.setForeground(new java.awt.Color(61, 0, 246));
        labelPhone.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPhone.setText("Phone : 1234567890");
        contactPane.add(labelPhone);
        labelPhone.setBounds(280, 120, 260, 40);

        labelCompanyName.setFont(new java.awt.Font("Perpetua Titling MT", 1, 14)); // NOI18N
        labelCompanyName.setForeground(new java.awt.Color(61, 0, 246));
        labelCompanyName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCompanyName.setText("Company Name");
        contactPane.add(labelCompanyName);
        labelCompanyName.setBounds(330, 70, 190, 50);

        labelEmail.setFont(new java.awt.Font("Perpetua", 1, 14)); // NOI18N
        labelEmail.setForeground(new java.awt.Color(61, 0, 246));
        labelEmail.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelEmail.setText("Email : company-name@gmail.com");
        contactPane.add(labelEmail);
        labelEmail.setBounds(290, 160, 260, 40);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/S1 LOGO-medium.png"))); // NOI18N
        jLabel1.setOpaque(true);
        contactPane.add(jLabel1);
        jLabel1.setBounds(0, 10, 400, 299);

        javax.swing.GroupLayout menuWindowPanelLayout = new javax.swing.GroupLayout(menuWindowPanel);
        menuWindowPanel.setLayout(menuWindowPanelLayout);
        menuWindowPanelLayout.setHorizontalGroup(
            menuWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuWindowPanelLayout.createSequentialGroup()
                .addComponent(contactPane, javax.swing.GroupLayout.PREFERRED_SIZE, 885, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        menuWindowPanelLayout.setVerticalGroup(
            menuWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuWindowPanelLayout.createSequentialGroup()
                .addComponent(contactPane, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 885, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 550, Short.MAX_VALUE)
        );

        jMenuBar1.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Blue"));

        jMenu1.setBackground(new java.awt.Color(204, 204, 255));
        jMenu1.setText("File");

        menuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuItemNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/project-setup.png"))); // NOI18N
        menuItemNew.setText("New");
        menuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemNewActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemNew);

        menuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuItemOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/opened-folder.png"))); // NOI18N
        menuItemOpen.setText("Open");
        menuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOpenActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemOpen);

        menuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_DOWN_MASK));
        menuItemClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close-window.png"))); // NOI18N
        menuItemClose.setText("Close");
        menuItemClose.setToolTipText("");
        menuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCloseActionPerformed(evt);
            }
        });
        jMenu1.add(menuItemClose);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Help");
        jMenu2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/saveBtnIcon2.png"))); // NOI18N

        contactUsMenuItem.setText("Reference Doc");
        contactUsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactUsMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(contactUsMenuItem);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuWindowPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuWindowPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemNewActionPerformed
        openNewProject();
    }//GEN-LAST:event_menuItemNewActionPerformed

    private void menuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenActionPerformed
        openSavedProject();
    }//GEN-LAST:event_menuItemOpenActionPerformed

    private void menuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCloseActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuItemCloseActionPerformed

    private void contactUsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactUsMenuItemActionPerformed
    }//GEN-LAST:event_contactUsMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                } catch (UnsupportedLookAndFeelException ex) {
                    log.error("unable to set look and feel.",ex);
                }
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contactPane;
    private javax.swing.JMenuItem contactUsMenuItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelCompanyName;
    private javax.swing.JLabel labelEmail;
    private javax.swing.JLabel labelPhone;
    private javax.swing.JLabel labelWebsite;
    private javax.swing.JMenuItem menuItemClose;
    private javax.swing.JMenuItem menuItemNew;
    private javax.swing.JMenuItem menuItemOpen;
    private javax.swing.JPanel menuWindowPanel;
    // End of variables declaration//GEN-END:variables

    private void openNewProject() {
        DataCalculator dc = new DataCalculator();
        dc.setVisible(true);
    }

    private void openSavedProject() {
        ProjectLoader projectLoader = new ProjectLoader();
        projectLoader.setVisible(true);
        /*try {
            File selectedFile=new File("");
            String location = "";
            String appDataFolder = System.getenv("APPDATA");
            if (!appDataFolder.isEmpty() && !appDataFolder.isBlank()) {
                List<String> projectFileNames = new ArrayList<>();
                Path dir = Paths.get(appDataFolder);
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.xml")) {
                    for (Path entry : stream) {
                        projectFileNames.add(entry.getFileName().toString());
                    }
                } catch (IOException e) {
                    log.error("Unable to read project folder", e);
                }
                selectedFile = new File("");
            } else {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("pscs", "pscs");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setCurrentDirectory(new File("D:/Programming/GUI/NB/PlumbIT/pdf"));
                int result = fileChooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                }
            }
            XStream xstream = DataInitializer.getXstream();
            PdfData projectData = (PdfData) xstream.fromXML(selectedFile);
            DataCalculator dc = new DataCalculator(projectData);
            dc.setVisible(true);
        } catch (HeadlessException e) {
            log.error("Unable to open project file.", e);
        }*/
    }
}
