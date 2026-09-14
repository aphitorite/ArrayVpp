package io.github.arrayv.panes;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.OfflineVideoRenderer;

/**
 * Modeless progress window shown while an offline {@link OfflineVideoRenderer}
 * render is running. Provides an indeterminate progress bar, a live frame
 * counter, and a cancel button.
 */
public final class RenderProgressDialog extends JDialog {
    private final Timer timer;

    public RenderProgressDialog(ArrayVisualizer arrayVisualizer, OfflineVideoRenderer renderer) {
        super(arrayVisualizer.getMainWindow(), "Rendering Video", false);
        
        JLabel label1 = new JLabel("Time elapsed: 00:00:00");
        JLabel label2 = new JLabel("Length: ________ frames (00:00:00)");

        JButton cancel = new JButton("Abort");
        cancel.addActionListener(e -> arrayVisualizer.setCanceled(true));

        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(label1, BorderLayout.NORTH);
        content.add(label2, BorderLayout.CENTER);
        content.add(cancel, BorderLayout.SOUTH);

        setContentPane(content);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(arrayVisualizer.getMainWindow());

        this.timer = new Timer(250, e -> {
            long frames = renderer.getFramesWritten();
            long length = frames / renderer.getFps();
            long seconds = renderer.getElapsedMillis() / 1000L;
            label1.setText("Time elapsed: " + String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60));
            label2.setText("Length: " + frames + " frames (" + String.format("%02d:%02d:%02d", length / 3600, (length % 3600) / 60, length % 60) + ")");
        });
        this.timer.start();
        setVisible(true);
    }

    public void close() {
        this.timer.stop();
        dispose();
    }
}
