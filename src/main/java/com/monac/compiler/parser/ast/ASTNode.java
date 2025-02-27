package com.monac.compiler.parser.ast;

import com.monac.compiler.visitors.ASTVisitor;

public interface ASTNode {
    <R> R accept(ASTVisitor<R> visitor);
}
