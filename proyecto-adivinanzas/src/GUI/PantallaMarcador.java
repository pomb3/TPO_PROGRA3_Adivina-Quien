package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PantallaMarcador extends JPanel {

    public PantallaMarcador(VentanaPrincipal ventana) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        add(Estilos.etiquetaSeccion("Marcador record"), BorderLayout.NORTH);

        List<String> lineas = ventana.getMarcador().ranking();
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(Estilos.FONT_MONO);
        if (lineas.isEmpty()) {
            area.setText("Todavia no gano ninguna partida completa.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String l : lineas) sb.append(l).append("\n");
            area.setText(sb.toString());
        }
        add(new JScrollPane(area), BorderLayout.CENTER);

        JButton btnVolver = Estilos.botonSecundario("Volver al inicio");
        btnVolver.addActionListener(e -> ventana.mostrarInicio());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pie.setOpaque(false);
        pie.add(btnVolver);
        add(pie, BorderLayout.SOUTH);
    }
}
