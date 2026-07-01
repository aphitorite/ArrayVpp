package io.github.arrayv.frames;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import io.github.arrayv.dialogs.RunScriptDialog;
import io.github.arrayv.main.ArrayManager;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.main.RunSort;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.prompts.ShufflePrompt;
import io.github.arrayv.prompts.SortPrompt;
import io.github.arrayv.prompts.VisualPrompt;
import io.github.arrayv.utils.Delays;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Sounds;
import io.github.arrayv.utils.Timer;

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

public final class UtilFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    public static int lastSortId = -1;

    private boolean auxCheckboxWarningShown = true; //set to false to enable warning

    private final int[] array;

    private final ArrayManager arrayManager;
    private final ArrayVisualizer arrayVisualizer;
    private final Delays Delays;
    private final Highlights Highlights;
    private final JFrame frame;
    private final Timer Timer;
    private final Sounds Sounds;
    
    private boolean lastUnlockedColor = false;
    private boolean lastUnlockedAux = false;

    private AppFrame abstractFrame;

    public UtilFrame(int[] array, ArrayVisualizer arrayVisualizer) {
        this.array = array;

        this.arrayVisualizer = arrayVisualizer;
        this.arrayManager = arrayVisualizer.getArrayManager();

        this.Delays = arrayVisualizer.getDelays();
        this.frame = arrayVisualizer.getMainWindow();
        this.Highlights = arrayVisualizer.getHighlights();
        this.Timer = arrayVisualizer.getTimer();
        this.Sounds = arrayVisualizer.getSounds();

        setUndecorated(true);
        initComponents();
        setLocation(Math.min((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getWidth(), frame.getX() + frame.getWidth()), frame.getY() + 29);
        setAlwaysOnTop(false);
        setVisible(true);
    }

    public void reposition(ArrayFrame af){
        toFront();
        setLocation(Math.min((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - getWidth(), frame.getX() + frame.getWidth() + af.getWidth()), frame.getY() + 29);
        if (this.abstractFrame != null && abstractFrame.isVisible())
            abstractFrame.reposition();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        // Variables declaration - do not modify//GEN-BEGIN:variables
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        this.modeBox = new javax.swing.JComboBox();
        this.visualButton = new javax.swing.JButton();
        this.colorCheckbox = new javax.swing.JCheckBox();
        this.auxCheckbox = new javax.swing.JCheckBox();
        JButton speedButton = new JButton();
        this.sortButton = new javax.swing.JButton();
        this.fastForwardButton = new javax.swing.JButton();
        this.stopButton = new javax.swing.JButton();
        this.rerunButton = new javax.swing.JButton();
        this.shuffleButton = new javax.swing.JButton();
        this.fixedDelayCheckbox = new javax.swing.JCheckBox();
        this.shuffleCheckbox = new javax.swing.JCheckBox();
        this.soundsCheckbox = new javax.swing.JCheckBox();
        this.softerSoundsCheckbox = new javax.swing.JCheckBox();
        this.endSweepCheckbox = new javax.swing.JCheckBox();
        JButton clearStatsButton = new JButton();
        this.statsCheckbox = new javax.swing.JCheckBox();
        this.realTimeCheckbox = new javax.swing.JCheckBox();

        jLabel1.setText("Settings");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        modeBox.setModel(new DefaultComboBoxModel<>(new String[] {
            "Sorting",
            "AntiQSort",
            "Stability Check",
            "Sorting Networks",
            "Reversed Sorting"
            // "*Simple* Benchmarking"
        }));
        modeBox.addActionListener(evt -> modeBoxActionPerformed());
        if (arrayVisualizer.isDisabledStabilityCheck()) {
            modeBox.removeItem("Stability Check");
        }

        visualButtonResetText();
        visualButton.addActionListener(evt -> visualButtonActionPerformed());

        colorCheckbox.setSelected(false);
        colorCheckbox.setText("Enable Color");
        colorCheckbox.addActionListener(evt -> colorCheckboxActionPerformed());

        auxCheckbox.setSelected(false);
        auxCheckbox.setText("Show Aux Arrays");
        auxCheckbox.addActionListener(evt -> auxCheckboxActionPerformed());

        speedButton.setText("Change Speed");
        speedButton.addActionListener(evt -> speedButtonActionPerformed());

        sortButtonResetText();
        sortButton.addActionListener(evt -> sortButtonActionPerformed());

        this.fastForwardButton.setText("\u25B6\u25B6");
        this.fastForwardButton.setToolTipText("Cancel Delays");
        this.fastForwardButton.setEnabled(false);
        this.fastForwardButton.addActionListener(evt -> cancelDelaysButtonActionPerformed());

        this.stopButton.setText("\u23F9");
        this.stopButton.setToolTipText("Cancel Sort");
        this.stopButton.setEnabled(false);
        this.stopButton.addActionListener(evt -> cancelSortButtonActionPerformed());

        this.rerunButton.setText("Re-run Sort");
        this.rerunButton.setEnabled(false);
        this.rerunButton.addActionListener(evt -> {
            if (lastSortId >= 0) {
                RunSort sortThread = new RunSort(arrayVisualizer);
                sortThread.runSort(array, lastSortId);
            }
        });

        shuffleButtonResetText();
        shuffleButton.addActionListener(evt -> shuffleButtonActionPerformed());

        fixedDelayCheckbox.setSelected(false);
        fixedDelayCheckbox.setText("Force Fixed Delays");
        fixedDelayCheckbox.addActionListener(evt -> fixedDelayCheckboxActionPerformed());

        shuffleCheckbox.setSelected(true);
        shuffleCheckbox.setText("Show Shuffle");
        shuffleCheckbox.addActionListener(evt -> shuffleCheckboxActionPerformed());

        soundsCheckbox.setSelected(true);
        soundsCheckbox.setText("Enable Sounds");
        soundsCheckbox.addActionListener(evt -> soundsCheckboxActionPerformed());

        softerSoundsCheckbox.setSelected(false);
        softerSoundsCheckbox.setText("Softer Sounds");
        softerSoundsCheckbox.addActionListener(evt -> softerSoundsCheckboxActionPerformed());

        endSweepCheckbox.setSelected(true);
        endSweepCheckbox.setText("End Sweep Anim");
        endSweepCheckbox.addActionListener(evt -> endSweepCheckboxActionPerformed());

        clearStatsButton.setText("Clear Stats");
        clearStatsButton.addActionListener(evt -> clearStatsButtonActionPerformed());

        statsCheckbox.setSelected(true);
        statsCheckbox.setText("Display Stats");
        statsCheckbox.addActionListener(evt -> statsCheckboxActionPerformed());

        realTimeCheckbox.setSelected(true);
        realTimeCheckbox.setText("Calc Real Time");
        realTimeCheckbox.addActionListener(evt -> realTimeCheckboxActionPerformed());

        JButton scriptButton = new JButton("Run Script");
        scriptButton.addActionListener(e -> {
            File scriptFile = new RunScriptDialog().getFile();
            if (scriptFile == null) return;
            try {
                arrayVisualizer.getScriptManager().runInThread(scriptFile);
            } catch (IOException e1) {
                JErrorPane.invokeErrorMessage(e1, "Run Script");
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, true)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, true)
                        .addComponent(jLabel1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
                            .addComponent(this.fixedDelayCheckbox)
                            .addComponent(this.shuffleCheckbox)
                            .addComponent(this.endSweepCheckbox)
                            .addComponent(this.realTimeCheckbox)
                            .addComponent(this.statsCheckbox)
                            .addComponent(this.soundsCheckbox)
                            .addComponent(this.colorCheckbox)
                            .addComponent(this.auxCheckbox)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, true)
                                .addComponent(this.softerSoundsCheckbox)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(this.fastForwardButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(this.stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(this.rerunButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(this.shuffleButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(clearStatsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(speedButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(this.visualButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(this.sortButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(scriptButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(this.modeBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, true)
                .addGroup(layout.createSequentialGroup()
                    .addGap(5, 5, 5)
                    .addComponent(jLabel1)
                    .addGap(7, 7, 7)
                    .addComponent(this.modeBox)
                    .addGap(10, 10, 10)
                    .addComponent(this.visualButton)
                    .addGap(5, 5, 5)
                    .addComponent(this.colorCheckbox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.auxCheckbox)
                    .addGap(7, 7, 7)
                    .addComponent(speedButton)
                    .addGap(12, 12, 12)
                    .addComponent(this.sortButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.rerunButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(this.fastForwardButton)
                        .addComponent(this.stopButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.shuffleButton)
                    .addGap(7, 7, 7)
                    .addComponent(this.fixedDelayCheckbox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.shuffleCheckbox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.soundsCheckbox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.softerSoundsCheckbox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.endSweepCheckbox)
                    .addGap(8, 8, 8)
                    .addComponent(clearStatsButton)
                    .addGap(5, 5, 5)
                    .addComponent(this.statsCheckbox)
                    .addComponent(this.realTimeCheckbox)
                    .addGap(5, 5, 5)
                    .addComponent(scriptButton)
                    .addGap(8, 8, 8))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void setMode(String mode) {
        this.modeBox.setSelectedItem(mode);
    }

    public void lockColorState(boolean val) {
    	if(colorCheckbox.isEnabled()) {
	    	this.lastUnlockedColor = colorCheckbox.isSelected();
	    	colorCheckbox.setSelected(val);
	    	colorCheckbox.setEnabled(false);
	        arrayVisualizer.toggleColor(colorCheckbox.isSelected());
    	}
    }
    public void setColorState(boolean val) {
    	colorCheckbox.setSelected(this.lastUnlockedColor = val);
        arrayVisualizer.toggleColor(colorCheckbox.isSelected());
    }
    public void unlockColorState() {
    	if(!colorCheckbox.isEnabled()) {
	    	colorCheckbox.setEnabled(true);
	    	colorCheckbox.setSelected(this.lastUnlockedColor);
	        arrayVisualizer.toggleColor(colorCheckbox.isSelected());
    	}
    }

    public void lockAuxState(boolean val) {
    	if(auxCheckbox.isEnabled()) {
	    	this.lastUnlockedAux = auxCheckbox.isSelected();
	    	auxCheckbox.setSelected(val);
	    	auxCheckbox.setEnabled(false);
	        arrayVisualizer.toggleExternalArrays(auxCheckbox.isSelected());
    	}
    }

    public void setAuxState(boolean val) {
    	auxCheckbox.setSelected(this.lastUnlockedAux = val);
	    arrayVisualizer.toggleExternalArrays(auxCheckbox.isSelected());
    }
    public void unlockAuxState() {
    	if(!auxCheckbox.isEnabled()) {
	    	auxCheckbox.setEnabled(true);
	    	auxCheckbox.setSelected(this.lastUnlockedAux);
	        arrayVisualizer.toggleExternalArrays(auxCheckbox.isSelected());
    	}
    }

    private void sortButtonActionPerformed() {//GEN-FIRST:event_jButton1ActionPerformed
        //CHANGE SORT
        if (this.abstractFrame != null && abstractFrame.isVisible()){
            boolean tmp = this.abstractFrame instanceof SortPrompt;
            abstractFrame.dispose();
            sortButtonResetText();
            if (tmp)
                return;
        }
        this.abstractFrame = new SortPrompt(this.array, this.arrayVisualizer, this.frame, this);
        sortButton.setText("Close");
        visualButtonResetText();
        shuffleButtonResetText();
    }//GEN-LAST:event_jButton1ActionPerformed

    public void sortButtonResetText() {
        sortButton.setText("Choose Sort");
    }

    public void sortButtonEnable() {
        sortButton.setEnabled(true);
        rerunButton.setEnabled(lastSortId >= 0);
        fastForwardButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    public void sortButtonDisable() {
        sortButton.setEnabled(false);
        rerunButton.setEnabled(false);
        fastForwardButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    public static void setLastSort(UtilFrame instance, int sortId) {
        lastSortId = sortId;
        if (instance != null && instance.rerunButton != null && !instance.arrayVisualizer.isActive()) {
            instance.rerunButton.setEnabled(true);
        }
    }

    private void visualButtonActionPerformed() {//GEN-FIRST:event_jButton2ActionPerformed
        //CHANGE VIEW
        if (this.abstractFrame != null && abstractFrame.isVisible()){
            boolean tmp = this.abstractFrame instanceof VisualPrompt;
            visualButtonResetText();
            abstractFrame.dispose();
            if (tmp)
                return;
        }
        this.abstractFrame = new VisualPrompt(this.arrayVisualizer, this.frame, this);
        visualButton.setText("Close");
        sortButtonResetText();
        shuffleButtonResetText();
    }//GEN-LAST:event_jButton2ActionPerformed

    public void visualButtonResetText() {
        visualButton.setText("Visual Style");
    }

    private void speedButtonActionPerformed() {//GEN-FIRST:event_jButton3ActionPerformed
        boolean speedPromptAllowed;

        if (this.abstractFrame == null) {
            speedPromptAllowed = true;
        } else {
            speedPromptAllowed = !this.abstractFrame.isVisible();
        }

        if (speedPromptAllowed) {
            boolean showPrompt = true;
            while (showPrompt) {
                try {
                    double oldRatio = Delays.getSleepRatio();
                    String userInput = JOptionPane.showInputDialog(null, "Modify the visual's speed below (Ex. 10 = Ten times faster)", oldRatio);
                    if (userInput != null) {
                        double newRatio = Double.parseDouble(userInput);
                        if (newRatio == 0) throw new Exception("Divide by zero");
                        Delays.setSleepRatio(newRatio);
                        Delays.updateCurrentDelay(oldRatio, Delays.getSleepRatio());
                    }
                    showPrompt = false;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Not a number! (" + e.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void fixedDelayCheckboxActionPerformed() {
        arrayVisualizer.toggleFixedDelays(fixedDelayCheckbox.isSelected());
    }

    private void shuffleCheckboxActionPerformed() {//GEN-FIRST:event_jCheckBox2ActionPerformed
        arrayVisualizer.toggleShuffleAnimation(shuffleCheckbox.isSelected());
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void endSweepCheckboxActionPerformed() {//GEN-FIRST:event_jCheckBox3ActionPerformed
        Highlights.toggleFancyFinishes(endSweepCheckbox.isSelected());
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void cancelDelaysButtonActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        Delays.changeSkipped(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void cancelSortButtonActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        arrayVisualizer.setCanceled(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void realTimeCheckboxActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        Timer.toggleRealTimer(realTimeCheckbox.isSelected());
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void clearStatsButtonActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        arrayVisualizer.resetAllStatistics();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void softerSoundsCheckboxActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        Sounds.setSofterSounds(softerSoundsCheckbox.isSelected());
    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void shuffleButtonActionPerformed() {//GEN-FIRST:event_jButton2ActionPerformed
        //CHANGE SIZE
        if (this.abstractFrame != null && abstractFrame.isVisible()){
            boolean tmp = this.abstractFrame instanceof ShufflePrompt;
            abstractFrame.dispose();
            shuffleButtonResetText();
            if (tmp)
                return;
        }
        this.abstractFrame = new ShufflePrompt(this.arrayManager, this.arrayVisualizer, this.frame, this);
        shuffleButton.setText("Close");
        sortButtonResetText();
        visualButtonResetText();
    }//GEN-LAST:event_jButton7ActionPerformed

    public void shuffleButtonResetText() {
        shuffleButton.setText("Choose Shuffle");
    }

    private void statsCheckboxActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        arrayVisualizer.toggleStatistics(statsCheckbox.isSelected());
    }//GEN-LAST:event_jCheckBox6ActionPerformed

    private void soundsCheckboxActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        Sounds.toggleSounds(soundsCheckbox.isSelected());
    }//GEN-LAST:event_jCheckBox7ActionPerformed

    private void colorCheckboxActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        arrayVisualizer.toggleColor(colorCheckbox.isSelected());
    }//GEN-LAST:event_jCheckBox8ActionPerformed

    private void auxCheckboxActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        if (!auxCheckboxWarningShown && auxCheckbox.isSelected()) {
            if (JOptionPane.showConfirmDialog(
                null,
                "<html>This will cause some sorts have extreme strobing/flashing."
                    + "<br><strong>It is highly recommended to NOT enable the \"" + auxCheckbox.getText() + "\" option if you may be at risk of seizures.</strong>"
                    + "<br>Are you sure you wish to enable this option?</html>",
                "Seizure Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            ) == JOptionPane.NO_OPTION) {
                auxCheckbox.setSelected(false);
                return;
            }
            auxCheckboxWarningShown = true;
        }
        arrayVisualizer.toggleExternalArrays(auxCheckbox.isSelected());
    }//GEN-LAST:event_jCheckBox8ActionPerformed

    private void modeBoxActionPerformed() {//GEN-FIRST:event_jButton4ActionPerformed
        //noinspection DataFlowIssue
        switch ((String)modeBox.getSelectedItem()) {
            case "Sorting":
                if (arrayVisualizer.enableBenchmarking(false))
                    break;
                shuffleButton.setEnabled(true);
                arrayVisualizer.setComparator(0);
                break;

            case "AntiQSort":
                if (arrayVisualizer.enableBenchmarking(false))
                    break;
                if (this.abstractFrame != null && abstractFrame.isVisible()){
                    abstractFrame.dispose();
                    shuffleButtonResetText();
                }
                shuffleButton.setEnabled(false);
                arrayVisualizer.setComparator(1);
                break;

            case "Stability Check":
                if (arrayVisualizer.enableBenchmarking(false))
                    break;
                shuffleButton.setEnabled(true);
                arrayVisualizer.setComparator(2);
                break;

            case "Sorting Networks":
                if (arrayVisualizer.enableBenchmarking(false))
                    break;
                shuffleButton.setEnabled(true);
                arrayVisualizer.setComparator(4);
                if (arrayVisualizer.getCurrentLength() > 1024) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Large sorting networks can take a long time (and high RAM usage) to visualize.\n" +
                            "A length of 1024 or less is recommended.",
                        "Sorting Network Visualizer", JOptionPane.WARNING_MESSAGE
                    );
                }
                break;

            case "Reversed Sorting":
                if (arrayVisualizer.enableBenchmarking(false))
                    break;
                shuffleButton.setEnabled(true);
                arrayVisualizer.setComparator(3);
                break;

            case "*Simple* Benchmarking":
                shuffleButton.setEnabled(true);
                arrayVisualizer.setComparator(0);
                arrayVisualizer.enableBenchmarking(true);
                break;
        }
    }//GEN-LAST:event_jCheckBox8ActionPerformed

    private javax.swing.JComboBox modeBox;
    private javax.swing.JButton visualButton;
    private javax.swing.JCheckBox colorCheckbox;
    private javax.swing.JCheckBox auxCheckbox;
    private javax.swing.JButton sortButton;
    private javax.swing.JButton shuffleButton;
    private javax.swing.JButton rerunButton;
    private javax.swing.JButton fastForwardButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JCheckBox fixedDelayCheckbox;
    private javax.swing.JCheckBox shuffleCheckbox;
    private javax.swing.JCheckBox soundsCheckbox;
    private javax.swing.JCheckBox softerSoundsCheckbox;
    private javax.swing.JCheckBox endSweepCheckbox;
    private javax.swing.JCheckBox statsCheckbox;
    private javax.swing.JCheckBox realTimeCheckbox;
}
