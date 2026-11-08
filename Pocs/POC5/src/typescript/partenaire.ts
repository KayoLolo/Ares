import { closeSync, constants, openSync, readSync, writeFileSync } from "node:fs";

function readLine(path: string): string {
    const input = openSync(path, constants.O_RDONLY);
    const byte = Buffer.alloc(1);
    const bytes: number[] = [];

    try {
        while (readSync(input, byte, 0, 1, null) > 0) {
            if (byte[0] === 10) {
                return Buffer.from(bytes).toString("utf8");
            }
            bytes.push(byte[0]);
        }
        throw new Error("Retour à la ligne obligatoire");
    } finally {
        closeSync(input);
    }
}

function main(): void {
    const args = process.argv.slice(2);
    if (args.length !== 0 && args.length !== 2) {
        throw new Error("Usage : partenaire");
    }

    let request = "/tmp/ares_req";
    let reply = "/tmp/ares_rep";
    if (args.length === 2) {
        request = args[0];
        reply = args[1];
    }

    const message = readLine(request);
    const output = openSync(reply, constants.O_WRONLY);
    try {
        writeFileSync(output, message.toUpperCase() + "\n", "utf8");
    } finally {
        closeSync(output);
    }
}

try {
    main();
} catch (error) {
    console.error(String(error));
    process.exitCode = 1;
}
