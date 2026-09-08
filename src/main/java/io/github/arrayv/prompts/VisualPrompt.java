package io.github.arrayv.prompts;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import io.github.arrayv.frames.AppFrame;
import io.github.arrayv.frames.UtilFrame;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sortdata.VisualInfo;
import io.github.arrayv.visuals.features.HeatMap;

/*
 *
MIT License

Copyright (c) 2019 w0rthy
Copyright (c) 2021-2022 ArrayV Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

public final class VisualPrompt extends javax.swing.JFrame implements AppFrame {
    public class PlaceholderTextField extends JTextField {
        /**
         * Shamelessly copied from https://stackoverflow.com/a/16229082/8840278
         */
        private static final long serialVersionUID = 1L;
        private String placeholder;

        public String getPlaceholder() {
            return placeholder;
        }

        @Override
        protected void paintComponent(final Graphics pG) {
            super.paintComponent(pG);

            if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) {
                return;
            }

            final Graphics2D g = (Graphics2D) pG;
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(getDisabledTextColor());
            g.drawString(placeholder, getInsets().left, pG.getFontMetrics()
                .getMaxAscent() + getInsets().top);
        }

        public void setPlaceholder(final String s) {
            placeholder = s;
        }
    }

    private static int lastCategory = -1;

    private static final long serialVersionUID = 1L;
    
    private static final int ROW = 3;

    private ArrayVisualizer arrayVisualizer;
    private JFrame frame;
    private UtilFrame utilFrame;
    private IdentityHashMap<String, javax.swing.JCheckBox> jCheckBoxes;
    private IdentityHashMap<String, JSpinner> jSpinners;
    private IdentityHashMap<String, javax.swing.JComponent> jCells;
    
    private javax.swing.JCheckBox byIndex(int i) {
        String id = arrayVisualizer.getVisualFeatures()[i].getListID();
        if (jCheckBoxes.containsKey(id)) return jCheckBoxes.get(id);
        int q = arrayVisualizer.queryFeatureState(id);
        javax.swing.JCheckBox n = new javax.swing.JCheckBox();
        n.setText(arrayVisualizer.getVisualFeatures()[i].getListName());
        n.setSelected(q > 0);
        n.setEnabled(Math.abs(q) < 2);
        n.addActionListener(evt -> {
            arrayVisualizer.setVisualFeature(id, n.isSelected() ? 1 : -1);
            for (String I : jCheckBoxes.keySet()) {
                int v = arrayVisualizer.queryFeatureState(I);
                if (jCheckBoxes.get(I).isSelected() ^ (v > 0)) {
                    jCheckBoxes.get(I).setSelected(v > 0);
                }
                jCheckBoxes.get(I).setEnabled(Math.abs(v) < 2);
            }
        });
        jCheckBoxes.put(id, n);
        return n;
    }

    private JSpinner spinnerFor(int i) {
        String id = arrayVisualizer.getVisualFeatures()[i].getListID();
        if (!"heat".equals(id)) return null;
        if (jSpinners.containsKey(id)) return jSpinners.get(id);
        JSpinner sp = new JSpinner(new SpinnerNumberModel(HeatMap.bShift, 0, 6, 1));
        sp.setToolTipText("Heat map: Cache line size");
        JFormattedTextField field = ((JSpinner.DefaultEditor) sp.getEditor()).getTextField();
        field.setColumns(1);
        field.setPreferredSize(new Dimension(12, 14));
        field.setMinimumSize(new Dimension(12, 14));
        sp.setPreferredSize(new Dimension(36, 18));
        sp.setMinimumSize(new Dimension(36, 18));
        sp.setMaximumSize(new Dimension(36, 18));
        for (java.awt.Component c : sp.getComponents()) {
            if (c instanceof JButton) {
                c.setPreferredSize(new Dimension(12, 8));
                c.setMinimumSize(new Dimension(12, 8));
                c.setMaximumSize(new Dimension(12, 8));
            }
        }
        sp.addChangeListener((ChangeListener) evt -> HeatMap.bShift = (Integer) sp.getValue());
        jSpinners.put(id, sp);
        return sp;
    }

    private javax.swing.JComponent cellFor(int i) {
        String id = arrayVisualizer.getVisualFeatures()[i].getListID();
        if (jCells.containsKey(id)) return jCells.get(id);
        javax.swing.JCheckBox cb = byIndex(i);
        JSpinner sp = spinnerFor(i);
        javax.swing.JComponent cell = sp == null ? cb : new javax.swing.JPanel();
        if (sp != null) {
            cell.setLayout(new javax.swing.BoxLayout(cell, javax.swing.BoxLayout.LINE_AXIS));
            cell.add(cb);
            cell.add(sp);
        }
        jCells.put(id, cell);
        return cell;
    }

    public VisualPrompt(ArrayVisualizer arrayVisualizer, JFrame frame, UtilFrame utilFrame) {
        this.arrayVisualizer = arrayVisualizer;
        this.frame = frame;
        this.utilFrame = utilFrame;

        setAlwaysOnTop(true);
        setUndecorated(true);
        initComponents();
        if (lastCategory == -1) {
            for (lastCategory = 1; ; lastCategory++) {
                jComboBox1.setSelectedIndex(lastCategory);
                if (jComboBox1.getSelectedItem().equals("Bar Visuals")) {
                    break;
                }
            }
        } else {
            jComboBox1.setSelectedIndex(lastCategory);
        }
        jTextField1.requestFocusInWindow();
        loadVisuals();
        reposition();
        setVisible(true);
    }

    @Override
    public void reposition() {
        setLocation(frame.getX()+(frame.getWidth()-getWidth())/2, frame.getY()+(frame.getHeight()-getHeight())/2);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        this.jComboBox1 = new javax.swing.JComboBox();
        this.jScrollPane1 = new javax.swing.JScrollPane();
        this.jList1 = new javax.swing.JList();
        this.jTextField1 = new PlaceholderTextField();
        this.jCheckBoxes = new IdentityHashMap<>();
        this.jSpinners = new IdentityHashMap<>();
        this.jCells = new IdentityHashMap<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jComboBox1.setModel(new DefaultComboBoxModel<>(VisualInfo.getCategories(arrayVisualizer.getVisuals())));
        jComboBox1.insertItemAt("All Visuals", 0);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(this.jList1);

        jComboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1SelectionChanged(evt);
            }
        });

        jTextField1.setPlaceholder("Search");
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                jTextField1TextChanged(e);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                jTextField1TextChanged(e);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                jTextField1TextChanged(e);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        
        UnaryOperator<javax.swing.GroupLayout.ParallelGroup> boxesH = g -> {
            int n = arrayVisualizer.getVisualFeatures().length;
            for (int i = 0; i < n; i += ROW) {
                javax.swing.GroupLayout.SequentialGroup s = layout.createSequentialGroup().addGap(25, 25, 25);
                for (int j = i; j < i + ROW && j < n; j++) {
                    if (j != i) s.addGap(5, 5, 5);
                    s.addComponent(cellFor(j));
                }
                g.addGroup(Alignment.CENTER, s.addGap(25, 25, 25));
            }
            return g;
        };
        
        UnaryOperator<javax.swing.GroupLayout.SequentialGroup> boxesV = G -> {
            int n = arrayVisualizer.getVisualFeatures().length;
            javax.swing.GroupLayout.ParallelGroup p = layout.createParallelGroup(Alignment.CENTER);
            for (int j = 0; j < ROW; j++) {
                javax.swing.GroupLayout.SequentialGroup g = layout.createSequentialGroup();
                for (int i = j; i < n; i += ROW) {
                    g.addGap(5, 5, 5).addComponent(cellFor(i));
                }
                if (j < n) p.addGroup(g);
            }
            if (n > 0) G.addGroup(p);
            return G.addGap(25, 25, 25);
        };
        
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            boxesH.apply(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addComponent(this.jComboBox1)
                    .addGap(25, 25, 25))
                .addGroup(layout.createSequentialGroup()
                    .addGap(45, 45, 45)
                    .addComponent(this.jTextField1)
                    .addGap(45, 45, 45))
                .addGroup(layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addComponent(this.jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(25, 25, 25))
           )
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(boxesV.apply(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.jComboBox1))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.jTextField1))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(this.jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                ))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        @SuppressWarnings("rawtypes")
        String visualName = (String)((JList)evt.getSource()).getSelectedValue();
        VisualInfo vslNotFinal = null;
        for (VisualInfo visual : arrayVisualizer.getVisuals()) {
            if (visual.getListName().equals(visualName)) {
                vslNotFinal = visual;
                break;
            }
        }
        arrayVisualizer.setActiveVisual(vslNotFinal);
        switch(vslNotFinal.getColorability()) {
            case NEVER:
                utilFrame.lockColorState(false);
                break;
            case AGNOSTIC:
                utilFrame.unlockColorState();
                break;
            case ALWAYS:
                utilFrame.lockColorState(true);
                break;
        }
        if(vslNotFinal.isAuxable()) {
            utilFrame.unlockAuxState();
        } else {
            utilFrame.lockAuxState(false);
        }
        utilFrame.visualButtonResetText();
        dispose();
    }//GEN-LAST:event_jList1ValueChanged

    @SuppressWarnings("unchecked")
    private void loadVisuals() {
        int index = jComboBox1.getSelectedIndex();
        String category = (String)jComboBox1.getSelectedItem();
        ArrayList<String> visuals = new ArrayList<>();
        String searchTerms = jTextField1.getText().toLowerCase();
        boolean isSearching = !searchTerms.isEmpty();
        for (VisualInfo visual : arrayVisualizer.getVisuals()) {
            if (index == 0 || visual.getCategory().equals(category)) {
                if (isSearching && !visual.getListName().toLowerCase().contains(searchTerms)) continue;
                visuals.add(visual.getListName());
            }
        }
        jList1.setListData(visuals.toArray());
    }

    private void jComboBox1SelectionChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        loadVisuals();
        VisualPrompt.lastCategory = jComboBox1.getSelectedIndex();
    }//GEN-LAST:event_jList1ValueChanged

    private void jTextField1TextChanged(DocumentEvent e) {//GEN-FIRST:event_jList1ValueChanged
        if (e.getLength() == jTextField1.getText().length())
            jComboBox1.setSelectedIndex(0);
        loadVisuals();
    }//GEN-LAST:event_jList1ValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    @SuppressWarnings("rawtypes")
    private javax.swing.JComboBox jComboBox1;
    @SuppressWarnings("rawtypes")
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private PlaceholderTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
