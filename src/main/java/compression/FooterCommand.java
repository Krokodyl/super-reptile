package compression;

public class FooterCommand extends Command {

    int commandCount;
    int offset;
    
    LzAlgorithm algorithm;

    public FooterCommand(int commandCount, int lastOffset, LzAlgorithm algorithm) {
        this.commandCount = commandCount;
        this.offset = lastOffset;
        this.algorithm = algorithm;
    }

    public int getCommandCount() {
        return commandCount;
    }

    public void setCommandCount(int commandCount) {
        this.commandCount = commandCount;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public byte[] getBytes() {
        return algorithm.getBytes(this);
    }

    @Override
    public String toString() {
        return "FooterCommand{" +
                "commandCount=" + commandCount +
                ", offset=" + offset +
                ", algorithm=" + algorithm +
                '}';
    }
}
