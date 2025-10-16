# Juno Programming Language

**Juno** is a modern, C-inspired compiled language targeting the JVM.  
It focuses on simplicity, clarity, and modularity — providing a familiar syntax and structure for low-level programmers who want to explore JVM bytecode without fully diving into Java or Kotlin.

---

### Why Juno Exists

Juno began as an experiment — an attempt to build a C-like language that fits naturally on top of the JVM.  
It was written in Java because it made sense that a compiler for a *“write once, run everywhere”* platform should itself be able to do the same.  
And perhaps, because Java has been unfairly maligned, Juno is also a small act of appreciation — and defiance.

Juno isn’t a toy or an esolang; it’s a real, statically typed, compiled language built around a few clear goals:

- **Familiarity:** If you know C, you can read and write Juno.
- **Modularity:** Functions live in modules, not objects. You import what you need — nothing more.
- **Transparency:** No hidden magic, no runtime surprises, no overengineered abstractions.

---

### Project Status

Juno is in **early alpha**.  
The compiler is functional, though rough. Many core features (parsing, typing, bytecode generation) are complete, while others — particularly arrays and error reporting — are still being refined.

Despite its imperfections, Juno already demonstrates a full compilation pipeline:
lexing → parsing → AST → JVM bytecode emission → runnable `.class` output.

The language isn’t finished. But it works enough to explore, learn from, and build upon — and that’s what makes it worth sharing.

---

### Philosophy

> “I wrote Juno because I wanted to create my new favorite tool, and as a side effect I now understand all the hate about Java"

That sense of curiosity — the urge to build something from the ground up — defines Juno.  
It’s not trying to replace C, Java, or anything else. It’s a personal exploration of how clean syntax, modular design, and static typing can coexist in a language meant for experimentation and learning.

One day, if it will be completed, and Juno 1.0.0 will be released, I will employ it as my go to language for casual (and serious) programming.

If you enjoy language design, JVM internals, or simply want to see how far one person can take a compiler project — Juno is for you.
