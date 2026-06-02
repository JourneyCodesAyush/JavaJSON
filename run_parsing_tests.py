import subprocess
from pathlib import Path

JAR_FILE = "target/JavaJSON-0.5.1.jar"
TEST_DIR = Path("test_parsing")

passed = 0
failed = 0
skipped = 0

for json_file in sorted(TEST_DIR.glob("*.json")):
    print(f"\nTesting: {json_file.name}")

    result = subprocess.run(
        ["java", "-jar", JAR_FILE, "validate", str(json_file)],
        capture_output=True,
        text=True,
    )

    accepted = result.returncode == 0
    name = json_file.name

    if name.startswith("y"):
        ok = accepted          # must accept
    elif name.startswith("n"):
        ok = not accepted      # must reject
    else:                      # i_ files
        skipped += 1
        print("SKIP (implementation defined)")
        continue

    if ok:
        print("PASS")
        passed += 1
    else:
        expected = "PASS" if name.startswith("y") else "FAIL"
        got = "PASS" if accepted else "FAIL"
        print(f"WRONG — expected {expected}, got {got}")
        if result.stderr:
            print(result.stderr.strip())
        failed += 1

print("\n=== Summary ===")
print(f"Passed:  {passed}")
print(f"Failed:  {failed}")
print(f"Skipped: {skipped}")
print(f"Total:   {passed + failed + skipped}")