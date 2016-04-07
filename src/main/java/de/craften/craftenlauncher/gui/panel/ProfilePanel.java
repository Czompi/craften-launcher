/**
 * CraftenLauncher is an alternative Launcher for Minecraft developed by Mojang.
 * Copyright (C) 2014  Johannes "redbeard" Busch, Sascha "saschb2b" Becker
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Profile Class:
 * <p>
 * Displays the users information
 *
 * @author saschb2b
 */

package de.craften.craftenlauncher.gui.panel;

import de.craften.craftenlauncher.exception.CraftenLogicException;
import de.craften.craftenlauncher.gui.MainController;
import de.craften.craftenlauncher.logic.Facade;
import de.craften.craftenlauncher.logic.auth.MinecraftUser;
import de.craften.ui.swingmaterial.MaterialButton;
import de.craften.ui.swingmaterial.MaterialColor;
import de.craften.ui.swingmaterial.MaterialShadow;
import de.craften.ui.swingmaterial.Roboto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class ProfilePanel extends JPanel {
    private static final Logger LOGGER = LogManager.getLogger(ProfilePanel.class);
    private JLabel _version;
    private JLabel _serverIP;
    private JLabel _ram;
    //private JComboBox<String> _cbVersions;

    private Font _fontPlain = new Font(Font.SANS_SERIF, Font.PLAIN, 16),
            _fontPlainSmall = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

    public ProfilePanel() {
        setBackground(Color.WHITE);
        setLayout(null);
    }

    public void init() {
        removeAll();
        addProfileInformation();
        repaint();

        if (Facade.getInstance().isQuickPlay()) {
            MainController.getInstance().play();
        }
    }

    private void addProfileInformation() {
        MinecraftUser user = null;
        try {
            user = Facade.getInstance().getUser();
        } catch (CraftenLogicException e) {
            LOGGER.error("Grab username error", e);
        }

        if (user != null) {
            //PlayerName
            JLabel playerName = new JLabel(user.getUsername());
            playerName.setFont(Roboto.MEDIUM.deriveFont(20f));
            playerName.setSize(238, 60);
            playerName.setLocation(69, 2);
            playerName.setVerticalAlignment(JLabel.TOP);
            playerName.setHorizontalAlignment(JLabel.CENTER);
            playerName.setForeground(MaterialColor.LIGHT_BLACK);
            add(playerName);

            JLabel playerMail = new JLabel(user.getEmail());
            playerMail.setFont(Roboto.REGULAR.deriveFont(12f));
            playerMail.setSize(238, 60);
            playerMail.setLocation(69, 26);
            playerMail.setVerticalAlignment(JLabel.TOP);
            playerMail.setHorizontalAlignment(JLabel.CENTER);
            playerMail.setForeground(MaterialColor.MIN_BLACK);
            add(playerMail);
        }

        //TODO logout button
        JLabel _logout = new JLabel("Logout");
        _logout.setFont(_fontPlainSmall);
        _logout.setSize(new Dimension(40, 20));
        //_logout.setLocation(playerName.getX(), playerName.getY() + playerName.getHeight() - 20);
        _logout.setForeground(Color.WHITE);
        _logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent env) {
                MainController.getInstance().logout();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        _logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(_logout);

        //TODO this logic should not be here
        try {
            if (Facade.getInstance().getMinecraftArguments().containsKey("version")) {
                Facade.getInstance().setMinecraftVersion(Facade.getInstance().getMinecraftArgument("version"));
            } else {
                Facade.getInstance().setMinecraftVersion(Facade.getInstance().getMinecraftVersions().get(0));
            }
        } catch (CraftenLogicException e) {
            LOGGER.error(e);
        }

        //TODO display IP where it is visible
        //Auto-Connect IP
        try {
            if (Facade.getInstance().getMinecraftArguments().containsKey("server")) {
                _serverIP = new JLabel("Will join to: " + Facade.getInstance().getMinecraftArgument("server"));
            } else {
                _serverIP = new JLabel("");
            }
        } catch (CraftenLogicException e) {
            LOGGER.error("Could not get server argument", e);
        }
        _serverIP.setFont(_fontPlain);
        _serverIP.setSize(this.getWidth(), 15);
        _serverIP.setLocation(1000, 1000);
        _serverIP.setForeground(Color.WHITE);
        add(_serverIP);

        JButton playButton = new MaterialButton();
        playButton.setText("Play");
        playButton.setBackground(MaterialColor.CYAN_500);
        playButton.setForeground(Color.WHITE);
        playButton.setSize(238 + MaterialShadow.OFFSET_LEFT + MaterialShadow.OFFSET_RIGHT, 40 + MaterialShadow.OFFSET_TOP + MaterialShadow.OFFSET_BOTTOM);
        playButton.setLocation(69 - MaterialShadow.OFFSET_LEFT, 114 - MaterialShadow.OFFSET_TOP);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MainController.getInstance().play();
            }
        });
        playButton.addMouseListener (new MouseAdapter () {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        add(playButton);

        //RAM
        try {
            String ram;
            if (Facade.getInstance().getMinecraftArguments().containsKey("xmx")) {
                ram = Facade.getInstance().getMinecraftArgument("xmx").toUpperCase();
            } else {
                ram = "~" + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "M";
            }
            JLabel ramLabel = new JLabel("RAM: " + ram);
            ramLabel.setFont(Roboto.REGULAR.deriveFont(12f));
            ramLabel.setSize(238, 30);
            ramLabel.setLocation(69, playButton.getY() + playButton.getHeight() - 10);
            ramLabel.setForeground(MaterialColor.MIN_BLACK);
            ramLabel.setHorizontalAlignment(JLabel.RIGHT);
            add(ramLabel);
        } catch (CraftenLogicException e) {
            e.printStackTrace();
        }

        //Version
        try {
            JLabel ramLabel = new JLabel("Version: " + Facade.getInstance().getMinecraftVersion().getVersion());
            ramLabel.setFont(Roboto.REGULAR.deriveFont(12f));
            ramLabel.setSize(238, 30);
            ramLabel.setLocation(69, playButton.getY() + playButton.getHeight() - 10);
            ramLabel.setForeground(MaterialColor.MIN_BLACK);
            ramLabel.setHorizontalAlignment(JLabel.LEFT);
            add(ramLabel);
        } catch (CraftenLogicException e) {
            e.printStackTrace();
        }
    }
}
