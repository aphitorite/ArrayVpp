package io.github.arrayv.panes;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        JLabel label = new JLabel("Rendering...");

        JButton cancel = new JButton("Cancel Render");
        cancel.addActionListener(e -> arrayVisualizer.setCanceled(true));

        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(label, BorderLayout.NORTH);
        content.add(bar, BorderLayout.CENTER);
        content.add(cancel, BorderLayout.SOUTH);

        setContentPane(content);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(arrayVisualizer.getMainWindow());

        this.timer = new Timer(250, e -> {
            long frames = renderer.getFramesWritten();
            long seconds = renderer.getElapsedMillis() / 1000L;
            label.setText("Rendering... " + frames + " frames (" + seconds + "s)");
        });
        this.timer.start();
        setVisible(true);
    }

    public void close() {
        this.timer.stop();
        dispose();
    }
}
