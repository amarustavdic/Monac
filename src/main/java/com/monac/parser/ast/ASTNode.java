package com.monac.parser.ast;

import com.monac.visitors.ASTVisitor;

public interface ASTNode {
    <R> R accept(ASTVisitor<R> visitor);
}
