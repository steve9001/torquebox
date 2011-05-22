package org.torquebox.core.injection.analysis;

import java.util.Stack;

import org.jboss.logging.Logger;
import org.jboss.msc.inject.InjectionException;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.Node;
import org.jruby.ast.NodeType;
import org.jruby.ast.StrNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.VCallNode;

public abstract class AbstractInjectableHandler implements InjectableHandler, Service<InjectableHandler> {

    private Logger log = Logger.getLogger( this.getClass() );
    private String type;
    private int recognitionPriority = 0;

    public AbstractInjectableHandler(String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }
    
    public int getRecognitionPriority() {
        return this.recognitionPriority;
    }
    
    public void setRecognitionPriority(int priority) {
        this.recognitionPriority = priority;
    }
    


    protected static String getString(Node node) {
        String str = null;

        if (node.getNodeType() == NodeType.STRNODE) {
            str = ((StrNode) node).getValue().toString();
        } else if (node.getNodeType() == NodeType.SYMBOLNODE) {
            str = ((SymbolNode) node).getName();
        } else if (node.getNodeType() == NodeType.ARRAYNODE) {
            if (((ArrayNode) node).size() == 1) {
                str = getString( ((ArrayNode) node).get( 0 ) );
            }
        }
        
        return str;
    }

    protected static String getJavaClassName(Node node) {
        Node cur = node;

        Stack<String> stack = new Stack<String>();

        while (cur != null) {
            if (cur.getNodeType() == NodeType.CALLNODE) {
                stack.push( ((CallNode) cur).getName() );
            } else if (cur.getNodeType() == NodeType.VCALLNODE) {
                stack.push( ((VCallNode) cur).getName() );
            }

            if (cur.childNodes().isEmpty()) {
                cur = null;
            } else {
                cur = cur.childNodes().get( 0 );
            }
        }

        StringBuilder name = new StringBuilder();

        while (!stack.isEmpty()) {
            name.append( stack.pop() );
            if (!stack.isEmpty()) {
                name.append( "." );
            }
        }

        return name.toString();
    }
    
    
    @Override
    public AbstractInjectableHandler getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public void start(StartContext context) throws StartException {
    }

    @Override
    public void stop(StopContext context) {
        
    }

}