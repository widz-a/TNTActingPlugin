const fs = require("fs");
const path = require("path");

const branch = process.argv[2];
const repo = process.argv[3]
const compareUrl = `https://api.github.com/repos/${repo}/compare/${process.argv[4]}`
const success = process.argv[5] === "true";

const { GITHUB_TOKEN, DISCORD_WEBHOOK } = process.env

function send(version, number, name) {
    fetch(compareUrl, { method: "GET", headers: { Authorization: `token ${GITHUB_TOKEN}` }})
        .then(res => res.json())
        .then(res => {
            let description = "";

            description += "**Branch:** " + branch;
            description += "\n**Status:** " + (success ? "success" : "failure");

            let changes = "\n\n**Changes:**";
            let hasChanges = false;
            for (let i in res.commits) {
                let commit = res.commits[i];

                changes += `\n- [\`${commit.sha.substring(0, 7)}\`](https://github.com/${repo}/commit/${commit.sha}) *${commit.commit.message}*`;
                hasChanges = true;
            }
            if (hasChanges) description += changes;

            if (success) description += `\n\n**Download:** [${name.toLowerCase()} v${version}-${number}](https://repo.withicality.xyz/snapshots/${repo.toLowerCase()}/${name.toLowerCase()}/${version}-${number}/${name.toLowerCase()}-${version}-${number}.jar)`;

            const webhook = {
                embeds: [
                    {
                        title: `${name} v${version} build #${number}`,
                        description,
                        url: `https://github.com/${repo}`,
                            color: success ? 2672680 : 13117480
                    }
                ]
            };

            fetch(DISCORD_WEBHOOK, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(webhook)
            });
        });
}

if (success) {
    let filename = "";
    fs.readdirSync("../../build/libs").forEach(file => {
        if (!file.endsWith("-all.jar") && !file.endsWith("-sources.jar")) filename = file;
    });

    const [name, version, build] = filename.split("-")
    send(version, build.slice(0, -4), name)
}
else {
    console.log("Boo hoo")
    process.exit(143)
}