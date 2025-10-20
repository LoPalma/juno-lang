#!/usr/bin/env python3

import subprocess
import time
import statistics

def time_command(command, runs=5):
    """Run a command multiple times and return timing statistics."""
    times = []
    
    for i in range(runs):
        start = time.time()
        result = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True
        )
        end = time.time()
        
        elapsed_ms = (end - start) * 1000
        times.append(elapsed_ms)
        
        # Check if command succeeded
        if result.returncode != 0:
            print(f"Warning: Command failed on run {i+1}")
            print(f"Error: {result.stderr}")
    
    return {
        'mean': statistics.mean(times),
        'median': statistics.median(times),
        'min': min(times),
        'max': max(times),
        'stdev': statistics.stdev(times) if len(times) > 1 else 0,
        'times': times
    }

def format_stats(name, stats):
    """Pretty print timing statistics."""
    print(f"\n{name}:")
    print(f"  Mean:   {stats['mean']:.2f} ms")
    print(f"  Median: {stats['median']:.2f} ms")
    print(f"  Min:    {stats['min']:.2f} ms")
    print(f"  Max:    {stats['max']:.2f} ms")
    print(f"  StdDev: {stats['stdev']:.2f} ms")
    print(f"  All runs: {[f'{t:.2f}' for t in stats['times']]}")

def main():
    print("=== Language Compilation Benchmark ===\n")
    
    # Number of runs for each test
    RUNS = 10
    
    # Commands to benchmark
    tests = [
        ("Python (interpreted)", "python3 hello.py"),
        ("Juno (compile + run)", "jpm run hello.juno"),
        ("Juno (compile only)", "jpm build hello.juno"),
        ("Juno (run only)", "jpm exec Hello"),
        ("Java (compile)", "javac Hello.java"),
        ("Java (run)", "java Hello"),
    ]
    
    results = {}
    
    for name, command in tests:
        print(f"Testing: {name} ({RUNS} runs)...", end=" ", flush=True)
        try:
            stats = time_command(command, runs=RUNS)
            results[name] = stats
            print(f"✓ (avg: {stats['mean']:.2f} ms)")
        except FileNotFoundError:
            print("✗ (command not found)")
        except Exception as e:
            print(f"✗ (error: {e})")
    
    # Print detailed results
    print("\n" + "="*50)
    print("DETAILED RESULTS")
    print("="*50)
    
    for name in results:
        format_stats(name, results[name])
    
    # Print comparison
    print("\n" + "="*50)
    print("COMPARISON (fastest to slowest)")
    print("="*50)
    
    sorted_results = sorted(results.items(), key=lambda x: x[1]['mean'])
    
    fastest = sorted_results[0][1]['mean']
    
    for i, (name, stats) in enumerate(sorted_results, 1):
        ratio = stats['mean'] / fastest
        print(f"{i}. {name:30s} {stats['mean']:7.2f} ms  ({ratio:.2f}x)")

if __name__ == "__main__":
    main()
