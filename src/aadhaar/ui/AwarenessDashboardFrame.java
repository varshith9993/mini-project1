package aadhaar.ui;

import aadhaar.backend.AwarenessService;
import aadhaar.backend.AwarenessService.AppContent;
import aadhaar.backend.AwarenessService.CheckAccountSection;
import aadhaar.backend.AwarenessService.ComparisonPoint;
import aadhaar.backend.AwarenessService.LanguageOption;
import aadhaar.backend.AwarenessService.QuizQuestion;
import aadhaar.backend.AwarenessService.ResourceItem;
import aadhaar.backend.AwarenessService.UiCopy;
import aadhaar.backend.AwarenessService.VideoItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class AwarenessDashboardFrame extends JFrame {
    private enum Module {
        LEARN,
        CHECK_ACCOUNT,
        VIDEOS,
        GOVERNMENT,
        QUIZ,
        RESOURCES
    }

    private final AwarenessService service;
    private final Path assetsDir;

    private LanguageOption currentLanguage = LanguageOption.ENGLISH;
    private Module currentModule = Module.LEARN;
    private AppContent currentContent;

    public AwarenessDashboardFrame(AwarenessService service, Path assetsDir) {
        this.service = service;
        this.assetsDir = assetsDir;
        this.currentContent = service.getContent(currentLanguage);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 820));
        setSize(1320, 900);
        setLocationRelativeTo(null);
        rebuildUi();
    }

    private void rebuildUi() {
        currentContent = service.getContent(currentLanguage);
        UiCopy ui = currentContent.ui();
        setTitle(ui.windowTitle());

        JPanel frameRoot = new JPanel(new BorderLayout());
        frameRoot.setBackground(Theme.APP_BACKGROUND);
        frameRoot.add(buildHeader(ui), BorderLayout.NORTH);
        frameRoot.add(buildContentScroller(ui), BorderLayout.CENTER);

        setContentPane(frameRoot);
        revalidate();
        repaint();
    }

    private JComponent buildHeader(UiCopy ui) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Theme.SURFACE);
        wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        JPanel top = new JPanel(new BorderLayout(18, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(18, 28, 18, 28));
        top.add(buildBrandBlock(ui), BorderLayout.WEST);
        top.add(buildLanguagePicker(ui), BorderLayout.EAST);

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(buildNavigationBar(ui), BorderLayout.SOUTH);
        return wrapper;
    }

    private JComponent buildBrandBlock(UiCopy ui) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        panel.setOpaque(false);

        JLabel logo = new JLabel(loadScaledIcon("aadhaar-logo.png", 96, 56));
        panel.add(logo);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(ui.appTitle());
        title.setFont(Theme.titleFont(27));
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(ui.appSubtitle());
        subtitle.setFont(Theme.bodyFont(14));
        subtitle.setForeground(Theme.TEXT_SECONDARY);

        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);
        panel.add(textPanel);
        return panel;
    }

    private JComponent buildLanguagePicker(UiCopy ui) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel(ui.languageLabel());
        label.setFont(Theme.bodyFont(14));
        label.setForeground(Theme.TEXT_SECONDARY);

        JComboBox<LanguageOption> comboBox = new JComboBox<>(LanguageOption.values());
        comboBox.setSelectedItem(currentLanguage);
        comboBox.setFont(Theme.bodyFont(15));
        comboBox.setPreferredSize(new Dimension(190, 34));
        comboBox.addActionListener(event -> {
            LanguageOption selected = (LanguageOption) comboBox.getSelectedItem();
            if (selected != null && selected != currentLanguage) {
                currentLanguage = selected;
                rebuildUi();
            }
        });

        panel.add(label);
        panel.add(comboBox);
        return panel;
    }

    private JComponent buildNavigationBar(UiCopy ui) {
        GradientPanel nav = new GradientPanel();
        nav.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        nav.setBorder(new EmptyBorder(0, 24, 0, 24));
        nav.setPreferredSize(new Dimension(0, 54));

        nav.add(createNavButton(ui.learnTab(), Module.LEARN));
        nav.add(createNavButton(ui.accountTab(), Module.CHECK_ACCOUNT));
        nav.add(createNavButton(ui.videoTab(), Module.VIDEOS));
        nav.add(createNavButton(ui.governmentTab(), Module.GOVERNMENT));
        nav.add(createNavButton(ui.quizTab(), Module.QUIZ));
        nav.add(createNavButton(ui.resourcesTab(), Module.RESOURCES));
        return nav;
    }

    private JButton createNavButton(String text, Module module) {
        boolean active = module == currentModule;
        JButton button = new JButton(text);
        button.setFont(Theme.bodyFont(15));
        button.setForeground(active ? Theme.TEXT_PRIMARY : Color.WHITE);
        button.setBackground(active ? Theme.ACCENT : new Color(0, 0, 0, 0));
        button.setOpaque(active);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(active ? Theme.ACCENT : new Color(255, 255, 255, 70), 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(event -> {
            currentModule = module;
            rebuildUi();
        });
        return button;
    }

    private JComponent buildContentScroller(UiCopy ui) {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.APP_BACKGROUND);
        body.setBorder(new EmptyBorder(22, 32, 28, 32));

        body.add(buildPhaseStrip(ui));
        body.add(Box.createVerticalStrut(20));
        body.add(buildActiveModule(ui));
        body.add(Box.createVerticalStrut(20));

        JLabel footer = new JLabel(ui.footerNote());
        footer.setFont(Theme.bodyFont(13));
        footer.setForeground(Theme.TEXT_SECONDARY);
        footer.setAlignmentX(LEFT_ALIGNMENT);
        body.add(footer);

        JScrollPane scroller = new JScrollPane(body);
        scroller.setBorder(null);
        scroller.getVerticalScrollBar().setUnitIncrement(16);
        scroller.getViewport().setBackground(Theme.APP_BACKGROUND);
        return scroller;
    }

    private JComponent buildPhaseStrip(UiCopy ui) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(LEFT_ALIGNMENT);

        JLabel trail = new JLabel("→ " + ui.phaseTrail());
        trail.setFont(Theme.bodyFont(15));
        trail.setForeground(Theme.TEXT_SECONDARY);

        JPanel steps = new JPanel(new GridLayout(1, 3, 16, 0));
        steps.setOpaque(false);
        steps.add(createStepCard("1", ui.learnTab(), currentModule == Module.LEARN));
        steps.add(createStepCard("2", ui.accountTab(), currentModule == Module.CHECK_ACCOUNT));
        steps.add(createStepCard("3", ui.videoTab(), currentModule == Module.VIDEOS));

        wrapper.add(trail);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(steps);
        return wrapper;
    }

    private JComponent createStepCard(String step, String label, boolean active) {
        RoundedPanel panel = new RoundedPanel(active ? Theme.PRIMARY : Theme.SURFACE_ALT, active ? Theme.PRIMARY : Theme.BORDER, 24);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 16));

        JLabel badge = new JLabel(step, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(active ? new Color(255, 255, 255, 45) : Theme.PRIMARY_SOFT);
        badge.setForeground(active ? Color.WHITE : Theme.PRIMARY_DARK);
        badge.setFont(Theme.titleFont(18));
        badge.setPreferredSize(new Dimension(34, 34));

        JLabel text = new JLabel(label);
        text.setFont(Theme.titleFont(18));
        text.setForeground(active ? Color.WHITE : Theme.TEXT_PRIMARY);

        panel.add(badge);
        panel.add(text);
        return panel;
    }

    private JComponent buildActiveModule(UiCopy ui) {
        return switch (currentModule) {
            case LEARN -> buildLearnPanel(ui);
            case CHECK_ACCOUNT -> buildCheckAccountPanel(ui);
            case VIDEOS -> buildVideosPanel(ui);
            case GOVERNMENT -> buildResourcePanel(ui.governmentTab(), currentContent.governmentLinks(), ui.openLinkButton(), ui.sectionSourcesSubtitle());
            case QUIZ -> buildQuizPanel(ui);
            case RESOURCES -> buildResourcePanel(ui.officialSourcesTitle(), currentContent.resources(), ui.openLinkButton(), ui.sectionSourcesSubtitle());
        };
    }

    private JPanel baseSectionPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;
    }

    private JComponent sectionHeading(String titleText, String subtitleText) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel(titleText);
        title.setFont(Theme.titleFont(34));
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("<html><body style='width:1100px'>" + subtitleText + "</body></html>");
        subtitle.setFont(Theme.bodyFont(16));
        subtitle.setForeground(Theme.TEXT_SECONDARY);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);
        return panel;
    }

    private ImageIcon loadScaledIcon(String fileName, int width, int height) {
        Path path = assetsDir.resolve(fileName);
        if (!Files.exists(path)) {
            return new ImageIcon();
        }
        ImageIcon icon = new ImageIcon(path.toString());
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void openLink(String url) {
        try {
            if (!Desktop.isDesktopSupported()) {
                throw new IllegalStateException("Desktop browsing is not supported on this system.");
            }
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Unable to open the link automatically.\n\n" + url + "\n\n" + exception.getMessage(), "Open Link", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JComponent buildLearnPanel(UiCopy ui) {
        JPanel panel = baseSectionPanel();
        panel.add(sectionHeading(currentContent.learn().title(), currentContent.learn().subtitle()));
        panel.add(Box.createVerticalStrut(20));

        JPanel topRow = new JPanel(new GridLayout(1, 2, 18, 0));
        topRow.setOpaque(false);
        topRow.add(buildLearnStoryCard(currentContent.learn().learnBullets()));
        topRow.add(buildKeyPointsCard(ui, currentContent.learn().keyPoints()));

        panel.add(topRow);
        panel.add(Box.createVerticalStrut(24));
        panel.add(buildComparisonTable(ui, currentContent.learn().comparisonPoints()));
        panel.add(Box.createVerticalStrut(18));

        JTextArea note = infoText(currentContent.learn().awarenessNote(), Theme.ACCENT_SOFT, Theme.WARNING);
        note.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(note);
        return panel;
    }

    private JComponent buildLearnStoryCard(List<String> bullets) {
        RoundedPanel card = new RoundedPanel(Theme.SURFACE, Theme.BORDER, 24);
        card.setLayout(new BorderLayout(18, 0));

        JLabel imageLabel = new JLabel(loadScaledIcon("learn-panel.png", 460, 255));
        card.add(imageLabel, BorderLayout.CENTER);

        JPanel bulletPanel = new JPanel();
        bulletPanel.setOpaque(false);
        bulletPanel.setLayout(new BoxLayout(bulletPanel, BoxLayout.Y_AXIS));
        bulletPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        for (String bullet : bullets) {
            bulletPanel.add(makeBulletLabel(bullet, Theme.TEXT_PRIMARY, 450));
            bulletPanel.add(Box.createVerticalStrut(10));
        }

        card.add(bulletPanel, BorderLayout.SOUTH);
        return card;
    }

    private JComponent buildKeyPointsCard(UiCopy ui, List<String> keyPoints) {
        RoundedPanel card = new RoundedPanel(Theme.ACCENT_SOFT, new Color(246, 221, 156), 24);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(ui.keyPointsTitle());
        title.setFont(Theme.titleFont(20));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(16));

        for (String point : keyPoints) {
            card.add(makeBulletLabel(point, Theme.TEXT_PRIMARY, 420));
            card.add(Box.createVerticalStrut(10));
        }

        JButton button = filledButton(ui.learnMoreButton(), Theme.PRIMARY);
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.addActionListener(event -> openLink("https://dbtbharat.gov.in/"));

        card.add(Box.createVerticalStrut(12));
        card.add(button);
        return card;
    }

    private JComponent buildComparisonTable(UiCopy ui, List<ComparisonPoint> rows) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(LEFT_ALIGNMENT);

        wrapper.add(sectionHeading(ui.comparisonTitle(), ui.comparisonSubtitle()));
        wrapper.add(Box.createVerticalStrut(14));

        DefaultTableModel model = new DefaultTableModel(new Object[]{ui.factorHeader(), ui.linkedHeader(), ui.dbtHeader(), ui.impactHeader()}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (ComparisonPoint row : rows) {
            model.addRow(new Object[]{row.factor(), row.aadhaarLinked(), row.dbtEnabled(), row.impact()});
        }

        JTable table = new JTable(model);
        table.setRowHeight(70);
        table.setFont(Theme.bodyFont(14));
        table.getTableHeader().setFont(Theme.titleFont(14));
        table.getTableHeader().setBackground(Theme.PRIMARY_SOFT);
        table.getTableHeader().setForeground(Theme.TEXT_PRIMARY);
        table.setGridColor(Theme.BORDER);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setVerticalAlignment(SwingConstants.TOP);
        renderer.setBorder(new EmptyBorder(10, 8, 10, 8));
        renderer.setForeground(Theme.TEXT_PRIMARY);
        for (int column = 0; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(Theme.roundedBorder(Theme.BORDER, 1, 0));
        scrollPane.setPreferredSize(new Dimension(1180, 340));
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);
        wrapper.add(scrollPane);
        return wrapper;
    }

    private JComponent buildCheckAccountPanel(UiCopy ui) {
        CheckAccountSection section = currentContent.checkAccount();
        JPanel panel = baseSectionPanel();
        panel.add(sectionHeading(section.title(), section.subtitle()));
        panel.add(Box.createVerticalStrut(20));

        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 0));
        grid.setOpaque(false);

        RoundedPanel questionsCard = new RoundedPanel(Theme.SURFACE, Theme.BORDER, 24);
        questionsCard.setLayout(new BoxLayout(questionsCard, BoxLayout.Y_AXIS));
        List<JCheckBox> checks = new ArrayList<>();
        for (String prompt : section.prompts()) {
            JCheckBox checkBox = new JCheckBox(prompt);
            checkBox.setOpaque(false);
            checkBox.setFont(Theme.bodyFont(15));
            checkBox.setForeground(Theme.TEXT_PRIMARY);
            checkBox.setAlignmentX(LEFT_ALIGNMENT);
            checks.add(checkBox);
            questionsCard.add(checkBox);
            questionsCard.add(Box.createVerticalStrut(12));
        }

        JTextArea resultArea = infoText(section.needsVisitBody(), Theme.SURFACE_ALT, Theme.TEXT_SECONDARY);
        resultArea.setBorder(Theme.roundedBorder(Theme.BORDER, 1, 16));
        resultArea.setAlignmentX(LEFT_ALIGNMENT);

        JButton assessButton = filledButton(ui.assessmentButton(), Theme.PRIMARY);
        assessButton.setAlignmentX(LEFT_ALIGNMENT);
        assessButton.addActionListener(event -> updateAssessmentResult(checks, section, resultArea, ui));

        questionsCard.add(Box.createVerticalStrut(8));
        questionsCard.add(assessButton);
        questionsCard.add(Box.createVerticalStrut(14));
        questionsCard.add(resultArea);

        RoundedPanel stepsCard = new RoundedPanel(Theme.ACCENT_SOFT, new Color(246, 221, 156), 24);
        stepsCard.setLayout(new BoxLayout(stepsCard, BoxLayout.Y_AXIS));
        JLabel stepsTitle = new JLabel(ui.nextStepsTitle());
        stepsTitle.setFont(Theme.titleFont(20));
        stepsTitle.setForeground(Theme.TEXT_PRIMARY);
        stepsTitle.setAlignmentX(LEFT_ALIGNMENT);
        stepsCard.add(stepsTitle);
        stepsCard.add(Box.createVerticalStrut(14));
        for (String step : section.nextSteps()) {
            stepsCard.add(makeBulletLabel(step, Theme.TEXT_PRIMARY, 420));
            stepsCard.add(Box.createVerticalStrut(10));
        }

        grid.add(questionsCard);
        grid.add(stepsCard);
        panel.add(grid);
        return panel;
    }

    private void updateAssessmentResult(List<JCheckBox> checks, CheckAccountSection section, JTextArea resultArea, UiCopy ui) {
        boolean linked = checks.get(0).isSelected();
        boolean seeded = checks.get(1).isSelected();
        boolean preferredBank = checks.get(2).isSelected();
        boolean schemeUsesDbt = checks.get(3).isSelected();

        String message;
        Color tone;
        if (!linked) {
            message = section.needsVisitTitle() + "\n\n" + section.needsVisitBody();
            tone = Theme.DANGER;
        } else if (!seeded) {
            message = section.linkedOnlyTitle() + "\n\n" + section.linkedOnlyBody();
            tone = Theme.WARNING;
        } else if (!preferredBank) {
            message = section.cautionTitle() + "\n\n" + section.cautionBody();
            tone = Theme.WARNING;
        } else {
            message = section.dbtEnabledTitle() + "\n\n" + section.dbtEnabledBody();
            if (!schemeUsesDbt) {
                message += "\n\n" + ui.dbtNotGuaranteedNote();
            }
            tone = Theme.SUCCESS;
        }

        resultArea.setText(message);
        resultArea.setForeground(tone);
    }

    private JComponent buildVideosPanel(UiCopy ui) {
        JPanel panel = baseSectionPanel();
        panel.add(sectionHeading(ui.videoTab(), "Official awareness pages and verified video references that open in your browser."));
        panel.add(Box.createVerticalStrut(20));

        JPanel grid = new JPanel(new GridLayout(1, currentContent.videos().size(), 16, 0));
        grid.setOpaque(false);
        for (VideoItem video : currentContent.videos()) {
            RoundedPanel card = new RoundedPanel(Theme.SURFACE, Theme.BORDER, 22);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.add(sourceBadge(video.source()));
            card.add(Box.createVerticalStrut(12));

            JLabel title = new JLabel("<html><body style='width:260px'>" + video.title() + "</body></html>");
            title.setFont(Theme.titleFont(18));
            title.setForeground(Theme.TEXT_PRIMARY);
            title.setAlignmentX(LEFT_ALIGNMENT);
            card.add(title);
            card.add(Box.createVerticalStrut(10));
            card.add(plainWrappedText(video.description()));
            card.add(Box.createVerticalGlue());
            card.add(Box.createVerticalStrut(12));

            JButton open = filledButton(ui.openVideoButton(), Theme.PRIMARY);
            open.setAlignmentX(LEFT_ALIGNMENT);
            open.addActionListener(event -> openLink(video.url()));
            card.add(open);
            grid.add(card);
        }
        panel.add(grid);
        return panel;
    }

    private JComponent buildResourcePanel(String titleText, List<ResourceItem> items, String actionLabel, String subtitle) {
        JPanel panel = baseSectionPanel();
        panel.add(sectionHeading(titleText, subtitle));
        panel.add(Box.createVerticalStrut(20));

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (ResourceItem item : items) {
            RoundedPanel card = new RoundedPanel(Theme.SURFACE, Theme.BORDER, 20);
            card.setLayout(new BorderLayout(20, 0));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            card.setAlignmentX(LEFT_ALIGNMENT);

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(sourceBadge(item.source()));
            text.add(Box.createVerticalStrut(10));

            JLabel title = new JLabel(item.title());
            title.setFont(Theme.titleFont(18));
            title.setForeground(Theme.TEXT_PRIMARY);
            title.setAlignmentX(LEFT_ALIGNMENT);
            text.add(title);
            text.add(Box.createVerticalStrut(8));
            text.add(plainWrappedText(item.description()));

            JButton open = filledButton(actionLabel, Theme.PRIMARY_DARK);
            open.addActionListener(event -> openLink(item.url()));

            card.add(text, BorderLayout.CENTER);
            card.add(open, BorderLayout.EAST);
            list.add(card);
            list.add(Box.createVerticalStrut(14));
        }

        panel.add(list);
        return panel;
    }

    private JComponent buildQuizPanel(UiCopy ui) {
        JPanel panel = baseSectionPanel();
        panel.add(sectionHeading(ui.quizTitle(), ui.quizSubtitle()));
        panel.add(Box.createVerticalStrut(20));

        JPanel questionList = new JPanel();
        questionList.setOpaque(false);
        questionList.setLayout(new BoxLayout(questionList, BoxLayout.Y_AXIS));

        List<ButtonGroup> groups = new ArrayList<>();
        List<List<JRadioButton>> optionRows = new ArrayList<>();
        int questionNumber = 1;

        for (QuizQuestion question : currentContent.quizQuestions()) {
            RoundedPanel card = new RoundedPanel(Theme.SURFACE, Theme.BORDER, 20);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setAlignmentX(LEFT_ALIGNMENT);

            JLabel prompt = new JLabel("<html><body style='width:1000px'>" + questionNumber + ". " + question.prompt() + "</body></html>");
            prompt.setFont(Theme.titleFont(18));
            prompt.setForeground(Theme.TEXT_PRIMARY);
            prompt.setAlignmentX(LEFT_ALIGNMENT);
            card.add(prompt);
            card.add(Box.createVerticalStrut(14));

            ButtonGroup group = new ButtonGroup();
            List<JRadioButton> row = new ArrayList<>();
            for (String option : question.options()) {
                JRadioButton radio = new JRadioButton(option);
                radio.setOpaque(false);
                radio.setFont(Theme.bodyFont(15));
                radio.setForeground(Theme.TEXT_PRIMARY);
                radio.setAlignmentX(LEFT_ALIGNMENT);
                group.add(radio);
                row.add(radio);
                card.add(radio);
                card.add(Box.createVerticalStrut(8));
            }

            groups.add(group);
            optionRows.add(row);
            questionList.add(card);
            questionList.add(Box.createVerticalStrut(14));
            questionNumber++;
        }

        RoundedPanel actions = new RoundedPanel(Theme.ACCENT_SOFT, new Color(246, 221, 156), 20);
        actions.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));
        JLabel resultLabel = new JLabel(" ");
        resultLabel.setFont(Theme.titleFont(16));
        resultLabel.setForeground(Theme.TEXT_PRIMARY);

        JButton submit = filledButton(ui.submitQuizButton(), Theme.PRIMARY);
        submit.addActionListener(event -> evaluateQuiz(ui, resultLabel, optionRows));

        JButton reset = filledButton(ui.resetQuizButton(), Theme.PRIMARY_DARK);
        reset.addActionListener(event -> {
            for (ButtonGroup group : groups) {
                group.clearSelection();
            }
            resultLabel.setText(" ");
        });

        actions.add(submit);
        actions.add(reset);
        actions.add(resultLabel);

        panel.add(questionList);
        panel.add(actions);
        return panel;
    }

    private void evaluateQuiz(UiCopy ui, JLabel resultLabel, List<List<JRadioButton>> optionRows) {
        int score = 0;
        StringBuilder builder = new StringBuilder("<html><body style='width:500px'>");
        for (int i = 0; i < currentContent.quizQuestions().size(); i++) {
            QuizQuestion question = currentContent.quizQuestions().get(i);
            int selectedIndex = -1;
            List<JRadioButton> radios = optionRows.get(i);
            for (int j = 0; j < radios.size(); j++) {
                if (radios.get(j).isSelected()) {
                    selectedIndex = j;
                    break;
                }
            }
            if (selectedIndex == question.correctIndex()) {
                score++;
            }
            builder.append("<b>").append(i + 1).append(".</b> ").append(question.explanation()).append("<br><br>");
        }
        builder.append("</body></html>");

        resultLabel.setText(ui.quizScorePrefix() + ": " + score + " / " + currentContent.quizQuestions().size());
        JOptionPane.showMessageDialog(this, new JLabel(builder.toString()), ui.quizTitle(), JOptionPane.INFORMATION_MESSAGE);
    }

    private JLabel makeBulletLabel(String text, Color color, int width) {
        JLabel label = new JLabel("<html><body style='width:" + width + "px'>• " + text + "</body></html>");
        label.setFont(Theme.bodyFont(15));
        label.setForeground(color);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JTextArea plainWrappedText(String text) {
        JTextArea area = new JTextArea(text);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setOpaque(false);
        area.setFont(Theme.bodyFont(15));
        area.setForeground(Theme.TEXT_SECONDARY);
        area.setAlignmentX(LEFT_ALIGNMENT);
        return area;
    }

    private JTextArea infoText(String text, Color background, Color foreground) {
        JTextArea area = new JTextArea(text);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(Theme.bodyFont(15));
        area.setForeground(foreground);
        area.setBackground(background);
        area.setOpaque(true);
        area.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return area;
    }

    private JLabel sourceBadge(String source) {
        JLabel label = new JLabel(source);
        label.setOpaque(true);
        label.setBackground(Theme.PRIMARY_SOFT);
        label.setForeground(Theme.PRIMARY_DARK);
        label.setFont(Theme.bodyFont(13));
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JButton filledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(Theme.bodyFont(15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(background.darker(), 1, true), BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        return button;
    }

    private static final class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            Theme.applyQuality(g2);
            g2.setPaint(new GradientPaint(0, 0, Theme.PRIMARY, getWidth(), getHeight(), Theme.PRIMARY_DARK));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class RoundedPanel extends JPanel {
        private final Color background;
        private final Color border;
        private final int arc;

        private RoundedPanel(Color background, Color border, int arc) {
            this.background = background;
            this.border = border;
            this.arc = arc;
            setOpaque(false);
            setBorder(new EmptyBorder(20, 20, 20, 20));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            Theme.applyQuality(g2);
            RoundRectangle2D shape = new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, arc, arc);
            g2.setColor(background);
            g2.fill(shape);
            g2.setColor(border);
            g2.draw(shape);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}
