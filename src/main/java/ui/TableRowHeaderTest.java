package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Vector;

class TableRowHeaderTest {
//    public static void main(String[] args) {
//        new TableRowHeaderFrame();
//    }
}

class TableRowHeaderFrame extends JFrame {
    public TableRowHeaderFrame() {
        DefaultTableModel model = new DefaultTableModel(50, 6);
        JTable table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        Vector bt = new Vector();
        bt.add("序号");
        bt.add("工号");
        bt.add("名字");
        bt.add("性别");

        Vector data1 = new Vector();
        data1.add("1");
        data1.add("10001");
        data1.add("xiaoming");
        data1.add("nan");
        Vector data2 = new Vector();
        data2.add("2");
        data2.add("10002");
        data2.add("xiaowang");
        data2.add("nv");
        Vector datas = new Vector();
        datas.add(data1);
        datas.add(data2);
        scrollPane.setRowHeaderView(new RowHeaderTable(table, 40));
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
        this.setSize(400, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}


class RowHeaderTable extends JTable {
    private JTable refTable;

    public RowHeaderTable(JTable refTable, int columnWidth) {
        super(new RowHeaderTableModel(refTable.getRowCount()));
        this.refTable = refTable;
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.getColumnModel().getColumn(0).setPreferredWidth(columnWidth);
        this.setDefaultRenderer(Object.class, new RowHeaderRenderer(refTable, this));
        this.setPreferredScrollableViewportSize(new Dimension(columnWidth, 0));
    }
}

class RowHeaderRenderer extends JLabel implements TableCellRenderer, ListSelectionListener {
    JTable reftable;
    JTable tableShow;

    public RowHeaderRenderer(JTable reftable, JTable tableShow) {
        this.reftable = reftable;
        this.tableShow = tableShow;

        ListSelectionModel listModel = reftable.getSelectionModel();
        listModel.addListSelectionListener(this);
    }

    public Component getTableCellRendererComponent(JTable table, Object obj,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {
        ((RowHeaderTableModel) table.getModel()).setRowCount(reftable.getRowCount());
        JTableHeader header = reftable.getTableHeader();
        this.setOpaque(true);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(CENTER);
        setBackground(header.getBackground());
        if (isSelect(row)) {
            setForeground(Color.white);
            setBackground(Color.lightGray);
        } else {
            setForeground(header.getForeground());
        }
        setFont(header.getFont());
        setText(String.valueOf(row + 1));
        return this;
    }

    public void valueChanged(ListSelectionEvent e) {
        this.tableShow.repaint();
    }

    private boolean isSelect(int row) {
        int[] sel = reftable.getSelectedRows();
        for (int i = 0; i < sel.length; i++)
            if (sel[i] == row)
                return true;
        return false;
    }
}


class RowHeaderTableModel extends AbstractTableModel {
    private int rowCount;

    public RowHeaderTableModel(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return 1;
    }

    public Object getValueAt(int row, int column) {
        return row;
    }
}