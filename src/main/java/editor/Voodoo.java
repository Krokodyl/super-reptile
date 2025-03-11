package editor;

import editor.common.Pointer;
import editor.common.PointerType;
import editor.common.Range;
import editor.table.ByteDataModel;
import editor.table.CustomRenderer;
import editor.table.RowHeaderModel;
import editor.table.RowHeaderRenderer;
import editor.tree.Block;
import editor.tree.BlockNodeRenderer;
import editor.tree.PointerDataModel;
import resources.Hex;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static resources.Hex.x;

public class Voodoo extends JFrame {
    private JPanel contentPane;
    private JPanel mainPanel;
    private JPanel panelTop;
    private JPanel panelBottom;
    private JButton loadFileButton;
    private JScrollPane scrollPane;
    
    private JTable dataTable;
    private JButton saveButton;
    private JButton testSelectButton;
    private JPanel panelRight;
    private JButton markRangeButton;
    private JPanel panelRange;
    private JLabel labelPointerOffset;
    private JLabel labelPointerValue;
    private JLabel labelPointerType;
    private JPanel panelMarkBlock;
    private JTextField a8TextField;
    private JButton valueFFButton;
    private JTextField textFieldTerminal;
    private JButton width8Button;
    private JButton widthXButton;
    private JButton customButton;
    private JButton customButton1;
    private JTextField textFieldBaseOffset;
    private JLabel labelPointerExtra;
    private JTree blockTree;
    private JButton loadBlocksButton;

    private ByteDataModel tableModel;
    private PointerDataModel pointerDataModel;

    CustomRenderer customRenderer;
    Range selection;
    
    private String[] COLUMNS = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F"};

    public Voodoo() {
        setContentPane(contentPane);
        //setModal(true);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser choose = new JFileChooser(
                        "D:\\emulation\\git\\tadaima\\src\\main\\resources\\data\\jpn-texts"
                );

                // Ouvrez le fichier
                int res = choose.showOpenDialog(null);
                // Enregistrez le fichier
                // int res = choose.showSaveDialog(null);

                if (res == JFileChooser.APPROVE_OPTION) {
                    File file = choose.getSelectedFile();
                    loadFile(file);
                    System.out.println(file.getAbsolutePath());
                }
            }
        });
        dataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = dataTable.rowAtPoint(e.getPoint());
                int col = dataTable.columnAtPoint(e.getPoint());
                selection.setOffsetStart(getOffset(row, col));
                selection.setOffsetEnd(getOffset(row, col));
                customRenderer.setSelectionEnabled(true);
                
                //updatePointerPanel(pointerDataModel.getPointer(Math.min(selection.getOffsetStart(), selection.getOffsetEnd())));
            }
        });
        dataTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int offsetEnd = selection.getOffsetEnd();
                int row = dataTable.rowAtPoint(e.getPoint());
                int col = dataTable.columnAtPoint(e.getPoint());
                int newOffsetEnd = getOffset(row, col);
                selection.setOffsetEnd(newOffsetEnd);
                
                if (offsetEnd!=newOffsetEnd) {
                    dataTable.repaint();
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser choose = new JFileChooser(
                        "D:\\emulation\\git\\tadaima\\src\\main\\resources\\data\\jpn-texts"
                );

                // Ouvrez le fichier
                int res = choose.showSaveDialog(null);
                // Enregistrez le fichier
                // int res = choose.showSaveDialog(null);

                if (res == JFileChooser.APPROVE_OPTION) {
                    File file = choose.getSelectedFile();
                    saveBlockFile(file);
                    System.out.println(file.getAbsolutePath());
                }
            }
        });
        testSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(scrollPane.getWidth());
                System.out.println(dataTable.getWidth());
                blockTree.repaint();
                /*tableModel.setPointerMetaData(selection);
                Pointer pointer = new Pointer(selection.getOffsetStart(), tableModel.getPointerValue(selection), PointerType.FIXED, 8);
                pointerDataModel.addPointer(selection, pointer);*/
                pack();
            }
        });
        dataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                System.out.println(e);
                customRenderer.setSelectionEnabled(false);
            }
        });
        width8Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int baseOffset = x(textFieldBaseOffset.getText());
                Pointer pointer = new Pointer(selection.getOffsetStart(), tableModel.getPointerValue(selection), PointerType.FIXED, 8);
                
                tableModel.markPointer(pointer, selection);
                int endOffset = tableModel.markData(pointer, baseOffset);

                pointerDataModel.addPointer(selection, pointer, baseOffset, endOffset);
            }
        });
        valueFFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int baseOffset = x(textFieldBaseOffset.getText());
                Pointer pointer = new Pointer(selection.getOffsetStart(), tableModel.getPointerValue(selection), PointerType.DELIMITED, 0xFFFF);
                
                tableModel.markPointer(pointer, selection);
                int endOffset = tableModel.markData(pointer, baseOffset);

                pointerDataModel.addPointer(selection, pointer, baseOffset, endOffset);
            }
        });
        customButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int baseOffset = x(textFieldBaseOffset.getText());
                Pointer pointer = new Pointer(selection.getOffsetStart(), tableModel.getPointerValue(selection), PointerType.DELIMITED, x(textFieldTerminal.getText()));
                
                tableModel.markPointer(pointer, selection);
                int endOffset = tableModel.markData(pointer, baseOffset);

                pointerDataModel.addPointer(selection, pointer, baseOffset, endOffset);
            }
        });
        loadBlocksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser choose = new JFileChooser(
                        "D:\\emulation\\git\\tadaima\\src\\main\\resources\\data\\jpn-texts"
                );

                // Ouvrez le fichier
                int res = choose.showOpenDialog(null);
                // Enregistrez le fichier
                // int res = choose.showSaveDialog(null);

                if (res == JFileChooser.APPROVE_OPTION) {
                    File file = choose.getSelectedFile();
                    loadBlockFile(file);
                    System.out.println(file.getAbsolutePath());
                }
            }
        });
    }

    private void saveBlockFile(File file) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            tableModel.saveModel(oos);
            pointerDataModel.saveModel(oos);
            //oos.writeObject(tableModel);
            //oos.writeObject(pointerDataModel);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadBlockFile(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            //tableModel = (ByteDataModel) oos.readObject();
            //pointerDataModel = (PointerDataModel) oos.readObject();
            tableModel.loadModel(ois);
            pointerDataModel.loadModel(ois);
            dataTable.repaint();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        Voodoo dialog = new Voodoo();
        dialog.pack();
        dialog.setVisible(true);
        //System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here 
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        tableModel = new ByteDataModel();
        pointerDataModel = new PointerDataModel();
        //tableModel.setColumnCount(16);
        //tableModel.setColumnIdentifiers(COLUMNS);
        
        blockTree = new JTree();
        blockTree.setModel(pointerDataModel);
        blockTree.setCellRenderer(new BlockNodeRenderer());
        
        dataTable = new JTable();
        dataTable.setModel(tableModel);
        //dataTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        dataTable.setCellSelectionEnabled(false);
        dataTable.setColumnSelectionAllowed(false);
        
        dataTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        String selectedData = null;

                        int[] selectedRow = dataTable.getSelectedRows();
                        int[] selectedColumns = dataTable.getSelectedColumns();

                        /*for (int i = 0; i < selectedRow.length; i++) {
                            for (int j = 0; j < selectedColumns.length; j++) {
                                selectedData = (String) dataTable.getValueAt(selectedRow[i], selectedColumns[j]);
                            }
                        }*/
                        //System.out.println("Selected: " + selectedData);
                    }
                }
        );
        
        //dataTable.setIntercellSpacing(new Dimension(10,10));
        
        selection = new Range();
        customRenderer = new CustomRenderer(selection);
        dataTable.setDefaultRenderer(Object.class, customRenderer);
        
        //scrollPane.setSize(new Dimension(550, scrollPane.getHeight()));
        setSize(new Dimension(800,800));
        setPreferredSize(new Dimension(800,800));
        dataTable.setSize(new Dimension(450, dataTable.getHeight()));
        repaint();
    }
    
    private void loadFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            /*Byte[] bytesObject = ArrayUtils.toObject(bytes);
            Object[][] data = Bytes.splitArray(bytesObject, 16);
            System.out.println(data.length);*/
            tableModel.loadData(bytes);
            pointerDataModel.setRoot(new Block(0, bytes.length, (byte)0));

            updateRowHeader(tableModel.getRowCount());
            
            contentPane.repaint();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void updateRowHeader(int lines) {
        
        RowHeaderModel headerModel = new RowHeaderModel();

        List<String> headers = new ArrayList<>();
        int i = 0;
        while (lines-->0) {
            headers.add(Hex.h8(i));
            i += 0x10;
        }
        headerModel.setHeaders(headers);
        
        JList rowHeader = new JList(headerModel);
        rowHeader.setFixedCellWidth(80);
        rowHeader.setFixedCellHeight(dataTable.getRowHeight()
                );
        //                           + table.getIntercellSpacing().height);
        rowHeader.setCellRenderer(new RowHeaderRenderer(dataTable));
        scrollPane.setRowHeaderView(rowHeader);
        
    }
    
    private void updatePointerPanel(Pointer p) {
        if (p!=null) {
            labelPointerOffset.setText(Hex.h(p.getOffset()));
            labelPointerType.setText(p.getType().name());
            labelPointerExtra.setText(String.valueOf(p.getExtra()));
            labelPointerValue.setText(String.valueOf(p.getValue()));
        }
    }

    public static int getOffset(int rowIndex, int columnIndex) {
        return rowIndex*0x10 + columnIndex;
    }
}
