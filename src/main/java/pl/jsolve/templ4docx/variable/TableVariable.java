package pl.jsolve.templ4docx.variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.jsolve.templ4docx.exception.IncorrectNumberOfRowsException;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.util.VariableType;

public class TableVariable implements Variable {

    private List<List<? extends Variable>> variables;
    private int numberOfRows = 0;

    public TableVariable() {
        this.variables = new ArrayList<List<? extends Variable>>();
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public List<List<? extends Variable>> getVariables() {
        return variables;
    }

    public void addVariable(List<? extends Variable> variable) {
        if (numberOfRows == 0) {
            numberOfRows = variable.size();
        } else if (numberOfRows != variable.size()) {
            throw new IncorrectNumberOfRowsException("Incorrect number of rows. Expected " + numberOfRows + " but was "
                    + variable.size());
        }
        this.variables.add(variable);
    }

    public Set<Key> getKeys() {
        return extract(variables);
    }

    private Set<Key> extract(List<List<? extends Variable>> variables) {
        Set<Key> keys = new HashSet<Key>();

        for (List<? extends Variable> variable : variables) {
            if (variable.isEmpty()) {
                break;
            }
            Variable firstVariable = variable.get(0);
            if (firstVariable instanceof TextVariable) {
                keys.add(new Key(((TextVariable) firstVariable).getKey(), VariableType.TEXT));
            } else if (firstVariable instanceof ImageVariable) {
                keys.add(new Key(((ImageVariable) firstVariable).getKey(), VariableType.IMAGE));
            } else if (firstVariable instanceof BulletListVariable) {
                keys.add(new Key(((BulletListVariable) firstVariable).getKey(), VariableType.BULLET_LIST));
            } else if (firstVariable instanceof ObjectVariable) {
                keys.add(new Key(((ObjectVariable) firstVariable).getKey(), VariableType.OBJECT));
            } else if (firstVariable instanceof TableVariable) {
                keys.addAll(extract(((TableVariable) firstVariable).getVariables()));
            }
        }

        return keys;
    }

    public boolean containsKey(String key) {
        return containsKey(variables, key);
    }

    private boolean containsKey(List<List<? extends Variable>> variables, String key) {

        for (List<? extends Variable> variable : variables) {
            if (variable.isEmpty()) {
                break;
            }
            Variable firstVariable = variable.get(0);
            if (firstVariable instanceof TextVariable) {
                if (key.equals(((TextVariable) firstVariable).getKey())) {
                    return true;
                }
            } else if (firstVariable instanceof ImageVariable) {
                if (key.equals(((ImageVariable) firstVariable).getKey())) {
                    return true;
                }
            } else if (firstVariable instanceof BulletListVariable) {
                if (key.equals(((BulletListVariable) firstVariable).getKey())) {
                    return true;
                }
            } else if (firstVariable instanceof ObjectVariable) {
                if (key.equals(((ObjectVariable) firstVariable).getKey())) {
                    return true;
                }
            } else if (firstVariable instanceof TableVariable) {
                boolean containsKey = containsKey(((TableVariable) firstVariable).getVariables(), key);
                if (containsKey) {
                    return true;
                }
            }
        }
        return false;
    }

    public Variable getVariable(Key key, int index) {
        return getVariable(variables, key, index);
    }

    private Variable getVariable(List<List<? extends Variable>> variables, Key key, int index) {
        for (List<? extends Variable> variable : variables) {
            if (variable.isEmpty() || variable.size() <= index) {
                break;
            }
            Variable firstVariable = variable.get(0);
            if (firstVariable instanceof TextVariable) {
                if (key.getKey().equals(((TextVariable) firstVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstVariable instanceof ImageVariable) {
                if (key.getKey().equals(((ImageVariable) firstVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstVariable instanceof BulletListVariable) {
                if (key.getKey().equals(((BulletListVariable) firstVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstVariable instanceof ObjectVariable) {
                if (key.getKey().equals(((ObjectVariable) firstVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstVariable instanceof TableVariable) {
                Variable foundVariable = getVariable(((TableVariable) firstVariable).getVariables(), key, index);
                if (foundVariable != null) {
                    return foundVariable;
                }
            }
        }
        return null;
    }
}
