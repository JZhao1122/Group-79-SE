package DeepManage_Front;

class DropTargetPanel extends JPanel implements DropTargetListener {
    private boolean dragOver = false;
    
    public DropTargetPanel() {
        setPreferredSize(new Dimension(400, 200));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        setBackground(new Color(240, 240, 240));
        new DropTarget(this, this);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dragOver) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(new Color(0, 200, 0, 50));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g.setColor(Color.GRAY);
        g.drawString("Drag and drop CSV/Excel file here", getWidth()/2-100, getHeight()/2);
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        dragOver = true;
        repaint();
    }
    
    @Override
    public void dragOver(DropTargetDragEvent dtde) {}
    
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {}
    
    @Override
    public void dragExit(DropTargetEvent dte) {
        dragOver = false;
        repaint();
    }
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        dragOver = false;
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable transferable = dtde.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> files = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (files.size() > 0) {
                    File file = files.get(0);
                    // 处理文件...
                    repaint();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
