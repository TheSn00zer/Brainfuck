import java.io.*;
import java.util.ArrayList;

public class Brainfuck
{
    private String _program;
    private ArrayList<Integer> _registers;
    private int _registerPointer = 0;
    private int _programPointer = 0;

    public static final String TYPE_FILE = "F";
    public static final String TYPE_TEXT = "T";
    public static final int INIT_REG_SIZE = 10;
    public static final char COM_DP_INC = '>';
    public static final char COM_DP_DEC = '<';
    public static final char COM_BYTE_INC = '+';
    public static final char COM_BYTE_DEC = '-';
    public static final char COM_OUTPUT = '.';
    public static final char COM_INPUT = ',';
    public static final char COM_JMP_FOR = '[';
    public static final char COM_JMP_BACK = ']';

    public static void main(String[] args)
    {
        if (args.length == 0) {
            System.out.println("Invalid args.");
            return;
        }
        try {
            Brainfuck bf;
            if (args.length == 1) {
                bf = new Brainfuck(args[0]);
            } else {
                bf = new Brainfuck(args[1], args[0]);
            }
            System.out.println(bf.run());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Brainfuck(String program) throws Exception
    {
        this(program, TYPE_TEXT);
    }

    public Brainfuck(String input, String type) throws Exception
    {
        switch(type) {
            case TYPE_FILE:
                _program = _getProgram(input);
                break;
            case TYPE_TEXT:
                _program = input;
                break;
            default:
                throw new Exception("Invalid type [" + type + "].");
        }
        _registers = new ArrayList<>(INIT_REG_SIZE);
    }

    public String run() throws Exception
    {
        String output = "";
        do {
            char command = getCurrentCommand();
            //System.out.println("Command is " + command);
            switch (command) {
                case COM_DP_INC:
                    incrementRegisterPointer();
                    break;
                case COM_DP_DEC:
                    decrementRegisterPointer();
                    break;
                case COM_BYTE_INC:
                    incrementRegisterValue();
                    break;
                case COM_BYTE_DEC:
                    decrementRegisterValue();
                    break;
                case COM_OUTPUT:
                    output += (char) getRegisterValue();
                    break;
                case COM_INPUT:
                    // TODO
                    throw new Exception("Command " + command + " not yet functional.");
                    //break;
                case COM_JMP_FOR:
                    if (getRegisterValue() == 0) {
                        int jumpBackToSkip = 0;
                        while (true) {
                            incrementProgramPointer();
                            char currentCommand = getCurrentCommand();
                            if (currentCommand == COM_JMP_FOR) {
                                jumpBackToSkip++;
                            } else if (currentCommand == COM_JMP_BACK) {
                                if (jumpBackToSkip == 0) {
                                    break;
                                }
                                jumpBackToSkip--;
                            }
                        }
                    }
                    break;
                case COM_JMP_BACK:
                    if (getRegisterValue() != 0) {
                        int jumpForwardToSkip = 0;
                        while (true) {
                            decrementProgramPointer();
                            char currentCommand = getCurrentCommand();
                            if (currentCommand == COM_JMP_BACK) {
                                jumpForwardToSkip++;
                            } else if (getCurrentCommand() == COM_JMP_FOR) {
                                if (jumpForwardToSkip == 0) {
                                    break;
                                }
                                jumpForwardToSkip--;
                            }
                        }
                    }
                    break;
                default:
                    //throw new Exception("Invalid command [" + command + "].");
                    break;
            }
            if (atEnd()) {
                break;
            }
            incrementProgramPointer();
        } while (true);
        return output;
    }

    private String _getProgram(String filePath) throws FileNotFoundException, IOException
    {
        String result;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        }
        return result;
    }

    public char getCurrentCommand()
    {
        return _program.charAt(_programPointer);
    }

    public boolean atEnd()
    {
        return _programPointer == _program.length() - 1;
    }

    protected void setRegisterValue(int value)
    {
        setRegisterValue(_registerPointer, value);
    }

    protected void setRegisterValue(int index, int value)
    {
        _expandRegister(index + 1);
        _registers.set(index, value);
    }

    protected int getRegisterValue()
    {
        return getRegisterValue(_registerPointer);
    }
    protected int getRegisterValue(int index)
    {
        _expandRegister(index + 1);
        return _registers.get(index);
    }

    private void _expandRegister(int size)
    {
        _registers.ensureCapacity(size);
        int registerSize = _registers.size();
        if (registerSize < size) {
            for (int i = registerSize; i < size; i++) {
                _registers.add(i, 0);
            }
        }
    }

    public int decrementProgramPointer() throws Exception
    {
        _programPointer--;
        if (_programPointer < 0) {
            throw new Exception("Invalid program pointer (" + _programPointer + ").");
        }
        return _programPointer;
    }

    public int incrementProgramPointer() throws Exception
    {
        _programPointer++;
        if (_programPointer >= _program.length()) {
            throw new Exception("Invalid program pointer (" + _programPointer + ").");
        }
        return _programPointer;
    }

    public int decrementRegisterValue()
    {
        int newValue = getRegisterValue() - 1;
        setRegisterValue(newValue);
        return newValue;
    }

    public int incrementRegisterValue()
    {
        int newValue = getRegisterValue() + 1;
        setRegisterValue(newValue);
        return newValue;
    }

    public int decrementRegisterPointer()
    {
        return --_registerPointer;
    }

    public int incrementRegisterPointer()
    {
        return ++_registerPointer;
    }

}
